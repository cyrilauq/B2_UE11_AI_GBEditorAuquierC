package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;
import org.helmo.gbeditor.infrastructures.exception.*;
import org.helmo.gbeditor.infrastructures.jdbc.ConnectionFactory;
import org.helmo.gbeditor.infrastructures.jdbc.UnableToSaveException;
import org.helmo.gbeditor.repositories.DataRepository;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.helmo.gbeditor.infrastructures.jdbc.SQLInstructions.*;

/**
 * Cette classe s'occupe de sauvegarder et récupérer un livre en base de données.
 */
public class BDRepository implements DataRepository {

    private Connection connection;

    private Session session;
    private final List<Book> allBooks = new ArrayList<>();
    private final List<BookDTO> allDtos = new ArrayList<>();
    private final SortedSet<String> existingIsbn = new TreeSet<>();

    private String author;
    private ConnectionFactory factory;

    private final Map<Book, BookDTO> tracker = new HashMap<>();

    /**
     * Créer un nouveau BDRepository sur base d'une connection et une session donnée.
     *
     * @param connection    Connection à la base de données.
     * @param session       Session courante.
     */
    public BDRepository(final Connection connection, final Session session) {
        this.connection = connection;
        this.session = session;
    }

    /**
     * Créer un nouveau BDRepository sur base d'une factory et d'un auteur donnée.
     *
     * @param factory
     * @param author
     */
    public BDRepository(final ConnectionFactory factory, final String author) {
        this.factory = factory;
        this.author = author;
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

    public void tearDown() throws SQLException {
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

    @Override
    public void add(final Book... books) {
        // TODO : Enlever la transaction ou l'utiliser correctement
        if(books != null) {
            List.of(books).forEach(b -> {
                var dto = Mapping.convertToBookDTO(b);
                Transaction
                        .from(factory.newConnection())
                        .commit((con) -> {
                            this.saveAuthorIfNotExists(dto.getAuthor());
                            this.saveBook(dto);
                        })
                        .onRollback((ex) -> {throw new UnableToSaveException(ex);})
                        .execute();
                tracker.put(b, dto);
            });
            loadBooks();
        }
    }

    private void savePage(final PageDTO dto, final int id_book) throws SQLException {
        try (PreparedStatement saveStmt = factory.newConnection().prepareStatement(INSERT_PAGE_STMT)) {
            if(id_book != -1) {
                saveStmt.setString(1, dto.getContent());
                saveStmt.setInt(2, id_book);
                saveStmt.setInt(3, dto.getNumPage());
                saveStmt.executeUpdate();
            }
        }
    }

    /**
     * Sauvegarde une page dans la base de données ou la modifie.
     *
     * @param dto           Page à ajouter
     * @param isbn          ISBN du livre auquel la page appartient
     *
     * @throws SQLException Si la page n'a pas pu être ajoutée ou modifiée
     */
    private void saveOrUpadatePage(final PageDTO dto, final String isbn) throws SQLException {
        var id_book = getIdBookForIsbn(isbn);
        if(pageExists(dto.getContent(), id_book)) {
            updatePage(dto, id_book);
        } else {
            savePage(dto, id_book);
        }
    }

    private void saveChoices(final PageDTO dto, final int id_book) throws SQLException {
        try (PreparedStatement saveStmt = factory.newConnection().prepareStatement(INSERT_CHOICES_STMT)) {
            dto.getChoices().forEach((c, p) -> {
                try {
                    saveStmt.setString(1, c);
                    saveStmt.setInt(2, getIdPageFor(dto.getContent(), id_book));
                    if(p.contains(": ") && p.startsWith("Page")) {
                        saveStmt.setInt(3, getIdPageFor(extractNPage(p), id_book));
                    } else {
                        saveStmt.setInt(3, getIdPageFor(p, id_book));
                    }
                    saveStmt.executeUpdate();
                } catch (SQLException e) {
                    throw new UnableToSavePageException(e.getMessage(), e);
                }
            });
        }
    }

    private int extractNPage(final String page) {
        var num = page.substring(page.indexOf(" ") + 1, page.contains(":") ? page.indexOf(":") : page.length());
        return Integer.parseInt(num);
    }

    private void updatePage(final PageDTO dto, final int id_book) throws SQLException {
        try (PreparedStatement saveStmt = factory.newConnection().prepareStatement(UPDATE_PAGE_STMT)) {
            if(id_book != -1) {
                saveStmt.setString(1, dto.getContent());
                var prevId = getIdPageFor(dto.getPrevPageContent(), id_book);
                var nextId = getIdPageFor(dto.getNextPageContent(), id_book);
                if(prevId == -1) {
                    saveStmt.setNull(2, Types.BIGINT);
                } else {
                    saveStmt.setInt(2, prevId);
                }
                if(nextId == -1) {
                    saveStmt.setNull(3, Types.BIGINT);
                } else {
                    saveStmt.setInt(3, nextId);
                }
                saveStmt.setInt(4, id_book);
                saveStmt.setString(5, dto.getContent());
                saveStmt.setInt(6, id_book);
                saveStmt.executeUpdate();
                saveChoices(dto, id_book);
            }
        }
    }

    private boolean pageExists(final String pageContent, final int id_book) throws SQLException {
        try(PreparedStatement stmt = factory.newConnection().prepareStatement(PAGE_EXISTS_STMT)) {
            stmt.setString(1, pageContent);
            stmt.setInt(2, id_book);
            return stmt.executeQuery().next();
        }
    }

    private void deletePageForBook(final int id_book) throws SQLException, UnableToSavePageException {
        try (PreparedStatement stmt = factory.newConnection().prepareStatement(DELETE_PAGE_FROM_BOOK_STMT)) {
            stmt.setInt(1,id_book);
            stmt.executeUpdate();
        }
    }

    private void saveBook(BookDTO dto) throws SQLException, UnableToSavePageException {
        try (PreparedStatement saveStmt = factory.newConnection().prepareStatement(INSERT_BOOK_STMT)) {
            saveStmt.setString(1, dto.getTitle());
            saveStmt.setString(2, dto.getIsbn());
            saveStmt.setString(3, dto.getResume());
            saveStmt.setString(4, dto.getImgPath());
            saveStmt.setString(5, dto.getAuthor());
            saveStmt.executeUpdate();

            Transaction
                    .from(factory.newConnection())
                    .commit((con) -> {
                        for (PageDTO p : dto) {
                            try {
                                saveOrUpadatePage(p, dto.getIsbn());
                            } catch (SQLException e) {
                                throw new SQLException(e);
                            }
                        }
                    })
                    .onRollback((ex) -> {throw new UnableToSavePageException("Une erreur est survenue lors de la sauvegarde des pages du livre " + dto.getTitle() + ".\nErreur: " + ex.getMessage(), ex);})
                    .execute();
            dto.forEach(p -> {
                try {
                    saveOrUpadatePage(p, dto.getIsbn());
                } catch (SQLException e) {
                    throw new UnableToSavePageException("Une erreur est survenue lors de la sauvegarde des pages du livre " + dto.getTitle() + ".\nErreur: " + e.getMessage(), e);
                }
            });
        }
    }

    private void updateBook(final Book book) throws SQLException {
        var dto = Mapping.convertToBookDTO(book);
        try (PreparedStatement saveStmt = factory.newConnection().prepareStatement(UPDATE_BOOKS_STMT)) {
            var id_book = getIdBookFor(book);
            deletePageForBook(id_book);
            saveStmt.setString(1, dto.getTitle());
            saveStmt.setString(2, dto.getResume());
            saveStmt.setString(3, dto.getIsbn());
            saveStmt.setString(4, dto.getImgPath());
            saveStmt.setString(5, dto.getAuthor());
            if(dto.getPublishDate() == null) {
                saveStmt.setTimestamp(6, null);
            } else {
                saveStmt.setTimestamp(6, Timestamp.valueOf(dto.getPublishDate()));
            }
            saveStmt.setInt(7, id_book);
            saveStmt.executeUpdate();
            dto.forEach(p -> {
                try {
                    saveOrUpadatePage(p, dto.getIsbn());
                } catch (SQLException e) {
                    throw new UnableToSavePageException("Une erreur est survenue lors de la sauvegarde des pages du livre " + dto.getTitle() + ".\nErreur: " + e.getMessage(), e);
                }
            });
            dto.forEach(p -> {
                try {
                    saveOrUpadatePage(p, dto.getIsbn());
                } catch (SQLException e) {
                    throw new UnableToSavePageException("Une erreur est survenue lors de la sauvegarde des pages du livre " + dto.getTitle() + ".\nErreur: " + e.getMessage(), e);
                }
            });
        }
    }

    private void saveAuthorIfNotExists(final String author) {
        if(!authorExists(author)) {
            try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(INSERT_AUTHOR_STMT, Statement.RETURN_GENERATED_KEYS)) {
                loadStmt.setString(1, author);
                loadStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean authorExists(final String author) {
        try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(AUTHOR_EXISTS_STMT)) {
            loadStmt.setString(1, author);
            var keys = loadStmt.executeQuery();
            return keys.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeBook(final String isbn) {
        try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(DELETE_BOOKS_WITH_ISBN_STMT)) {
            var toRemove = isbn.replaceAll("-", "");
            deletePageForBook(getIdBookForIsbn(toRemove));
            loadStmt.setString(1, toRemove);
            loadStmt.executeUpdate();
            existingIsbn.remove(toRemove);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getIdBookFor(final Book book) {
        var result = -1;
        for (final var entry: tracker.entrySet()) {
            if(entry.getKey() == book || entry.getKey().equals(book)) {
                result = entry.getValue().id;
            }
        }
        return result;
    }

    private int getIdBookForIsbn(final String isbn) {
            try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(SELECT_ID_BOOK_STMT)) {
                loadStmt.setString(1, isbn.replaceAll("-", ""));
                var keys = loadStmt.executeQuery();
                if(keys.next() && !keys.wasNull()) {
                    return keys.getInt(1);
                }
                keys.close();
                return -1;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    private int getIdPageFor(final String content, final int id_book) {
        try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(SELECT_PAGE_ID_STMT)) {
            loadStmt.setString(1, content);
            loadStmt.setInt(2, id_book);
            var keys = loadStmt.executeQuery();
            if(keys.next() && !keys.wasNull()) {
                return keys.getInt(1);
            }
            keys.close();
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getIdPageFor(final int num, final int id_book) {
        try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(SELECT_PAGE_ID_FROM_NUM_STMT)) {
            loadStmt.setInt(1, num);
            loadStmt.setInt(2, id_book);
            var keys = loadStmt.executeQuery();
            if(keys.next() && !keys.wasNull()) {
                return keys.getInt(1);
            }
            keys.close();
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Book book) {
        try {
            updateBook(book);
        } catch (SQLException e) {
            throw new UnableToSaveBookException("Une erreur est survenue lors de la sauvegarde du livre.\nErreur: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean remove(String... books) {
        Transaction
            .from(factory.newConnection())
            .commit((con) -> List.of(books).forEach(this::removeBook))
            .onRollback((ex) -> {throw new UnableToSaveException(ex);})
            .execute();
        return true;
    }

    @Override
    public List<Book> getBooks() {
        loadBooks();
        tracker.clear();
        allBooks.clear();
        allDtos.forEach(dto -> {
            var temp = Mapping.convertToBook(dto);
            allBooks.add(temp);
            tracker.put(temp, dto);
        });
        return new ArrayList<>(allBooks);
    }

    @Override
    public void loadBooks() {
        tracker.clear();
        existingIsbn.clear();
        allDtos.clear();
        try(PreparedStatement loadStmt = factory.newConnection().prepareStatement(SELECT_ALL_BOOKS_STMT)) {
            loadStmt.setString(1, author);
            ResultSet rs = loadStmt.executeQuery();
            var isbn = "";
            while (rs.next() && !rs.wasNull()) {
                isbn = rs.getString("isbn");
                var publishDate = rs.getTimestamp("datePublication");
                var tempDTO = new BookDTO(rs.getString("title"),
                        isbn,
                        rs.getString("author"),
                        rs.getString("resume"),
                        rs.getString("imgPath"),
                        BookDTO.CURRENT_VERSION, new ArrayList<>(getPageFor(isbn)),
                        publishDate == null ? null : publishDate.toLocalDateTime());
                tempDTO.id = rs.getInt("id_book");
                allDtos.add(tempDTO);
                tracker.put(Mapping.convertToBook(tempDTO), tempDTO);
                existingIsbn.add(isbn);
            }
        } catch (SQLException | UnableToConnectException e) {
            throw new UnableToOpenResourceException("Une erreur est survenue lors de la connexion à la base de données.", e);
        }
    }

    private Collection<PageDTO> getPageFor(final String isbn) throws SQLException {
        final List<PageDTO> result = new ArrayList<>();
        try(PreparedStatement stmt = factory.newConnection().prepareStatement(SELECT_PAGE_FROM_ISBN_STMT)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && !rs.wasNull()) {
                result.add(
                        new PageDTO(rs.getString("content"), getChoicesFor(rs.getInt("id_page")), rs.getInt("num_page"))
                );
            }
            rs.close();
        }
        return result;
    }

    private Map<String, String> getChoicesFor(final int id_page) throws SQLException {
        final Map<String, String> result = new TreeMap<>();
        try(PreparedStatement stmt = factory.newConnection().prepareStatement(SELECT_CHOICES_FROM_PAGE_STMT)) {
            stmt.setInt(1, id_page);
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && !rs.wasNull()) {
                result.put(rs.getString("content"), rs.getInt("targetP") + "");
            }
        }
        return result;
    }

    @Override
    public String getLastIsbn() {
        try(Statement stmt = factory.newConnection().createStatement()) {
            stmt.executeQuery(SELECT_LAST_ISBN_STMT);
            ResultSet rs = stmt.getResultSet();
            if (rs.next() && !rs.wasNull()) {
                return rs.getString("isbn");
            }
            return "0000000000";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book searchBookFor(String isbn) {
        for (final var b : allBooks) {
            if(b.getIsbn().equalsIgnoreCase(isbn)) {
                return b;
            }
        }
        return null;
    }
}
