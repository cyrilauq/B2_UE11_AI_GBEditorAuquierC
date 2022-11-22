package org.helmo.gbeditor.infrastructures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.repositories.DataRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Cette classe permet de manipuler des fichiers JSON.
 * Elle permet en outre de :
 * <ul>
 *     <li>Lire/écrir dans un fichier</li>
 *     <li>Stocker les données récupérer du fichier dans un objet JsonRepository</li>
 * </ul>
 */
public class JsonRepository implements DataRepository {
    private final Path path;
    private final String file;
    private final List<Book> books = new ArrayList<>();
    private final SortedSet<String> existingISBN = new TreeSet<>();
    private String currentAuthor = "";

    /**
     * Crée un nouvel JsonRepository avec un path donné.
     *
     * @param path  Chemin donnant accès au fichier JSON voulu.
     */
    public JsonRepository(final Path path, final String fileName) {
        this.path = path;
        file = fileName +  ".json";
    }

    @Override
    public void setCurrentAuthor(final String author) {
        this.currentAuthor = author;
    }

    @Override
    public List<Book> getBooks() {
        loadBooks();
        final List<Book> result = new ArrayList<>();
        books.forEach(b -> {
            if(b.getAuthor().equalsIgnoreCase(currentAuthor)) {
                result.add(b);
            }
        });
        return result;
    }

    @Override
    public void loadBooks() {
        books.clear();
        getData().forEach(dto -> {
            books.add(Mapping.convertToBook(dto));
            existingISBN.add(dto.getIsbn());
        });
    }

    /**
     * Paths.get(System.getProperty("user.home"), "ue36", path)
     * ==> Permet de récupérer le dossier home de l'utilisateur et de récupérer un fichier dans le dossier ue36
     *
     * @return
     */
    @Override
    public List<BookDTO> getData() {
        Path pathFile = Paths.get(path.toString(), file);
        fileExists(path.toString(), pathFile);
        try (BufferedReader r = Files.newBufferedReader(pathFile)) {
            var gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(BookDTO.class, new CustomDeserialize())
                    .create();
            List<BookDTO> temp = new Gson().fromJson(r, new TypeToken<List<BookDTO>>() {}.getType());
            if(temp == null) {
                return new ArrayList<>();
            }  else {
                Collections.sort(temp);
                return new ArrayList<>(temp);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fileExists(final String path, final Path pathFile) {
        dirExists(path);
        if(Files.notExists(pathFile)) {
            try {
                Files.createFile(pathFile);
            } catch (IOException ex) {
                throw new FileException("L'acquisition du fichier a échoué: " + ex.getMessage(), ex);
            }
        }
    }

    private void dirExists(final String path) {
        Path path1 = Paths.get(path);
        try {
            Files.createDirectory(path1);
        } catch (FileAlreadyExistsException ignored) {

        } catch (IOException ex) {
            throw new FileException("L'acquisition du dossier a échoué: ", ex);
        }
    }

    @Override
    public void add(final Book... books) {
        var pathFile = Paths.get(this.path.toAbsolutePath().toString(), this.file);
        var existingBooks = getData();
        this.books.addAll(List.of(books));
        List.of(books).forEach(b -> {
            var newB = Mapping.convertToBookDTO(b);
            if(existingISBN.contains(newB.getIsbn())) {
                throw new BookAlreadyExistsException("The books already exists.");
            }
            existingBooks.add(newB);
            existingISBN.add(newB.getIsbn());
        });
        Collections.sort(existingBooks);
        try(Writer w = Files.newBufferedWriter(pathFile, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            var gson = new Gson();
            gson.toJson(existingBooks, w);
        } catch (IOException e) {
            this.books.removeAll(List.of(books));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Book book) {
        var pathFile = Paths.get(path.toAbsolutePath().toString(), file);
        var existingBooks = getData();
        addDtoInBooks(book, existingBooks);
        Collections.sort(existingBooks);
        try(Writer w = Files.newBufferedWriter(pathFile, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            var gson = new Gson();
            gson.toJson(existingBooks, w);
        } catch (IOException e) {
            books.remove(book);
            throw new RuntimeException(e);
        }
    }

    private void addDtoInBooks(final Book book, List<BookDTO> existingBooks) {
        for(final var b : existingBooks) {
            if(b.getIsbn().equalsIgnoreCase(book.getIsbn())) {
                existingBooks.remove(b);
                existingBooks.add(Mapping.convertToBookDTO(book));
                break;
            }
        }
    }

    @Override
    public String getLastIsbn() {
        return existingISBN.isEmpty() ? "0000000000" : existingISBN.last();
    }

    @Override
    public Book searchBookFor(String isbn) {
        for(final var b : books) {
            if (b.hasIsbn(isbn)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public boolean remove(final String... books) {
        return false;
    }

    /**
     * Définit une erreur qui sera lancée lorsque le programme rencontrera une erreur lors de l'utilisation d'un fichier.
     */
    private class FileException extends RuntimeException {
        private FileException(final String message, final IOException ex) {
            super(message, ex);
        }
    }

    /**
     * Définit une erreur qui sera lancée lorsqu'un livre qu'on veut ajouter existe déjà.
     */
    public class BookAlreadyExistsException extends RuntimeException {

        /**
         * Crée une nouvelle erreur de type BookAlreadyExistsException
         *
         * @param msg   Message à donner à l'erreur.
         */
        public BookAlreadyExistsException(final String msg) { super(msg); }
    }
}
