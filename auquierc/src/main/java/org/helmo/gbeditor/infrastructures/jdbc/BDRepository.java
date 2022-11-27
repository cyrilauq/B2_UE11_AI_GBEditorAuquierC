package org.helmo.gbeditor.infrastructures.jdbc;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.BookFieldName;
import org.helmo.gbeditor.infrastructures.Mapping;
import org.helmo.gbeditor.infrastructures.Tracker;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;
import org.helmo.gbeditor.infrastructures.exception.*;
import org.helmo.gbeditor.repositories.DataRepository;
import org.helmo.gbeditor.repositories.exceptions.BookAlreadyExistsException;
import org.helmo.gbeditor.repositories.exceptions.DataManipulationException;
import org.helmo.gbeditor.repositories.exceptions.UnableToSavePageException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.helmo.gbeditor.infrastructures.jdbc.BookBDRepository.*;
import static org.helmo.gbeditor.infrastructures.jdbc.PageBDRepository.*;
import static org.helmo.gbeditor.infrastructures.jdbc.SQLInstructions.*;

/**
 * Cette classe s'occupe de sauvegarder et récupérer un livre en base de données.
 */
public class BDRepository implements DataRepository {

    private Connection connection;
    private final SortedSet<String> existingIsbn = new TreeSet<>();

    private String author;
    private final ConnectionFactory factory;
    private final Tracker tracker = new Tracker();

    /**
     * Créer un nouveau BDRepository sur base d'une factory et d'un auteur donnée.
     *
     * @param factory   Factory qui gère la création de la connection à la base de données.
     */
    public BDRepository(final ConnectionFactory factory) {
        this.factory = factory;
    }

    /**
     * Initialise la base de données de tests.
     */
    public void setUp() {
        executeStmt(CREATE_AUTHOR_STMT);
        executeStmt(CREATE_BOOK_STMT);
        executeStmt(CREATE_PAGE_STMT);
        executeStmt(CREATE_CHOICES_STMT);
    }

    private void executeStmt(final String stmt) {
        try(Statement execStmt = factory.newConnection().createStatement()) {
            execStmt.executeUpdate(stmt);
        } catch (SQLException e) {
            throw new UnableToTearDownException(e.getMessage(), e);
        }
    }

    /**
     * Supprime les tables de la base de données.
     */
    public void tearDown() {
        executeStmt(DROP_CHOICES_STMT);
        executeStmt(DROP_PAGE_STMT);
        executeStmt(DROP_BOOK_STMT);
        executeStmt(DROP_AUTHOR_STMT);
    }

    @Override
    public List<BookDTO> getData() {
        return null;
    }

    @Override
    public void setCurrentAuthor(String author) {
        this.author = author;
    }

    private boolean containsBook(final String isbn) {
        try(PreparedStatement stmt = connection.prepareStatement(SELECT_ID_BOOK_STMT)) {
            stmt.setString(1, isbn);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void add(final Book... books) {
        Transaction
                .from(connection = factory.newConnection())
                .commit((con) -> {
                    for(final var b : books) {
                        verifyIfBookExists(containsBook(Mapping.convertISBNToDTO(b.get(BookFieldName.ISBN))));
                        var dto = Mapping.convertToBookDTO(b);
                        saveAuthorIfNotExists(dto.getAuthor());
                        saveBook(dto);
                        tracker.put(b, dto);
                    }
                })
                .onRollback((ex) -> {throw new DataManipulationException("Une erreur est survenue lors de la sauvegarde du livre.", ex);})
                .execute();
        loadBooks();
        closeConnection();
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DataManipulationException("Erreur lors de la déconnexion à la base de donnée", e);
        }
    }

    private void deletePageForBook(final int id_book) throws SQLException, UnableToSavePageException {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_PAGE_FROM_BOOK_STMT)) {
            stmt.setInt(1,id_book);
            stmt.executeUpdate();
        }
    }

    private void saveBook(BookDTO dto) throws SQLException {
        int id_book = -1;
        try (PreparedStatement saveStmt = connection.prepareStatement(INSERT_BOOK_STMT, Statement.RETURN_GENERATED_KEYS)) {
            addDtoToInserStmt(dto, saveStmt);
            saveStmt.executeUpdate();
            var key = saveStmt.getGeneratedKeys();
            if(key.next() && !key.wasNull()) {
                dto.id = id_book = key.getInt(1);
            }

            // TODO : avant d'ajouter un livre vérifier que l'iSBN n'est pas déjà pris et lancé une exception si c'est le cas.
        }
        if(id_book != -1) {
            saveAllPages(dto);
        }
    }

    private void saveAllPages(final BookDTO dto) throws SQLException {
        try(final var stmt = connection.prepareStatement(INSERT_PAGE_STMT)) {
            addPageToStmt(dto, stmt);
        }
        addChoicesToPages(connection, dto, dto.id);
    }

    private void saveAuthorIfNotExists(final String author) {
        if(!authorExists(author)) {
            try(PreparedStatement loadStmt = connection.prepareStatement(INSERT_AUTHOR_STMT, Statement.RETURN_GENERATED_KEYS)) {
                loadStmt.setString(1, author);
                loadStmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataManipulationException(e);
            }
        }
    }

    private boolean authorExists(final String author) {
        try(PreparedStatement loadStmt = connection.prepareStatement(AUTHOR_EXISTS_STMT)) {
            loadStmt.setString(1, author);
            var keys = loadStmt.executeQuery();
            return keys.next();
        } catch (SQLException e) {
            throw new DataManipulationException(e);
        }
    }

    private void removeBook(final String isbn) {
        try(PreparedStatement loadStmt = connection.prepareStatement(DELETE_BOOKS_WITH_ISBN_STMT)) {
            var toRemove = Mapping.convertISBNToDTO(isbn.replaceAll("-", ""));
            deletePageForBook(getIdBookForIsbn(toRemove));
            loadStmt.setString(1, toRemove);
            loadStmt.executeUpdate();
            existingIsbn.remove(toRemove);
        } catch (SQLException e) {
            throw new DataManipulationException("Une erreur est survenu dans la suppression du livre.", e);
        }
    }

    private int getIdBookForIsbn(final String isbn) throws SQLException {
        try(PreparedStatement loadStmt = connection.prepareStatement(SELECT_ID_BOOK_STMT)) {
            loadStmt.setString(1, isbn.replaceAll("-", ""));
            return getFirstKey(loadStmt);
        }
    }

    @Override
    public void save(Book book) {
        verifyIfBookExists(!tracker.contains(book) && containsBook(book.get(BookFieldName.ISBN)));
        Transaction
                .from(connection = factory.newConnection())
                .commit((con) -> updateBook(book))
                .onRollback((ex) -> {throw new DataManipulationException("Une erreur est survenue lors de la sauvegarde du livre.", ex);})
                .execute();
        closeConnection();
    }

    private void verifyIfBookExists(boolean tracker) {
        if(tracker) {
            throw new BookAlreadyExistsException("Le livre existe déjà dans la base de données.");
        }
    }

    private void updateBook(final Book book) throws SQLException {
        var dto = Mapping.convertToBookDTO(book);
        try (PreparedStatement saveStmt = connection.prepareStatement(UPDATE_BOOKS_STMT)) {
            dto.id = tracker.getIdBookFor(book);
            deletePageForBook(dto.id);
            addDtoToUpdateStmt(dto, saveStmt);
            saveStmt.executeUpdate();
            saveAllPages(dto);
        }
    }

    @Override
    public boolean remove(String... books) {
        Transaction
                .from(connection = factory.newConnection())
                .commit((con) -> List.of(books).forEach(b -> {
                        removeBook(b);
                        tracker.remove(b);
                }))
                .onRollback((ex) -> {throw new DataManipulationException("Une erreur est survenue lors de la suppression du livre.", ex);})
                .execute();
        closeConnection();
        return true;
    }

    @Override
    public List<Book> getBooks() {
        loadBooks();
        return new ArrayList<>(tracker.getAllBooks());
    }

    @Override
    public void loadBooks() {
        tracker.clear();
        existingIsbn.clear();
        try(PreparedStatement loadStmt = (connection = factory.newConnection()).prepareStatement(SELECT_ALL_BOOKS_STMT)) {
            loadStmt.setString(1, author);
            loadDataFromStmt(loadStmt);
        } catch (SQLException | UnableToConnectException e) {
            throw new DataManipulationException("Une erreur est survenue lors de la récupération des données.", e);
        }
        closeConnection();
    }

    private void loadDataFromStmt(PreparedStatement loadStmt) throws SQLException {
        try (final var rs = loadStmt.executeQuery()) {
            while (rs.next() && !rs.wasNull()) {
                var tempDTO = convertResultSetToDTO(rs);
                tracker.put(Mapping.convertToBook(tempDTO), tempDTO);
                existingIsbn.add(tempDTO.getIsbn());
            }
        }
    }

    private List<PageDTO> getPageFor(final String isbn) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(SELECT_PAGE_FROM_ISBN_STMT)) {
            stmt.setString(1, isbn);
            return getPageFromStmt(stmt);
        }
    }

    @Override
    public String getLastIsbn() {
        try(Statement stmt = (connection = factory.newConnection()).createStatement()) {
            return getIsbnFrom(stmt);
        } catch (SQLException e) {
            throw new DataManipulationException("Le livre n'a pas pu être récupéré.", e);
        }
    }

    private String getIsbnFrom(final Statement stmt) throws SQLException {
        try(final var rs = stmt.executeQuery(SELECT_LAST_ISBN_STMT)) {
            if (rs.next() && !rs.wasNull()) {
                return rs.getString("isbn");
            }
            return "0000000000";
        }
    }

    @Override
    public Book searchBookFor(String isbn) {
        Book result = null;
        try(final var stmt = (connection = factory.newConnection()).prepareStatement(SELECT_BOOKS_WITH_ISBN_STMT)) {
            stmt.setString(1, Mapping.convertISBNToDTO(isbn));
            var tempDTO = convertResultSetToDTO(stmt);
            if(tempDTO != null) {
                tempDTO.pages = getPageFor(tempDTO.getIsbn());
                result = Mapping.convertToBook(tempDTO);
            }
        } catch (SQLException e) {
            throw new DataManipulationException("Une erreur est survenue lors de la récupération du livre ayant l'ISBN: " + isbn, e);
        }
        return result;
    }
}
