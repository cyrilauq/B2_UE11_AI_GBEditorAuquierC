package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.BookMetadata;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.repositories.DataRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonRepositoryTests {
    @Test
    public void getDataFromExistingFile() {
        var path = Paths.get("src", "test", "resources", "read_book").toAbsolutePath();
        final DataRepository jr = new JsonRepository(path, "books");
        List<BookDTO> expected = new ArrayList<>(List.of(
                new BookDTO("Moi2", "123455789", "Cyril", "Je me baladais en foret volume 2!!!!", "", null),
                new BookDTO("Moi", "123456789", "Cyril", "Je me baladais en foret", "", null)
        ));
        var actual = jr.getData();
        for(var b : expected) {
            actual.contains(b);
        }
        assertIterableEquals(expected, actual);
    }

    @Test
    public void getDataFromAWrongFormattedFile() throws URISyntaxException {
        var path = Paths.get(getClass().getResource("/read_book_non_existing").toURI()).toString();
        var filePath = Paths.get(path, "wrong_formatted.json");
        try {
            if(!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(var bw = Files.newBufferedWriter(Paths.get(path, "wrong_formatted"))) {
            bw.write("[,{");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final DataRepository jr = new JsonRepository(Path.of(path), "wrong_formatted");
        List<BookDTO> expected = new ArrayList<>();
        var actual = jr.getData();
        for(var b : expected) {
            actual.contains(b);
        }
        deleteFile(Paths.get(path, "wrong_formatted.json"));
        assertIterableEquals(expected, actual);
    }

    @Test
    public void getDataFromAWrongFormattedFileWithOneBook() {
        var path = Paths.get("src", "test", "resources", "read_book_non_existing").toAbsolutePath();
        try {
            if(!Files.exists(Paths.get(path.toString(), "wrong_formatted.json"))) {
                Files.createFile(Paths.get(path.toString(), "wrong_formatted.json"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(var bw = Files.newBufferedWriter(Paths.get(path.toString(), "wrong_formatted"))) {
            bw.write("[{\"title\": \"Moi\",\"isbn\": \"123456789\",\"resume\": \"Je me baladais en foret\",\"author\": \"Cyril\"},");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final DataRepository jr = new JsonRepository(path, "wrong_formatted");
        List<BookDTO> expected = new ArrayList<>();
        var actual = jr.getData();
        for(var b : expected) {
            actual.contains(b);
        }
        deleteFile(Paths.get(path.toString(), "wrong_formatted.json"));
        assertIterableEquals(expected, actual);
    }

    @Test
    public void getDataFromAWrongFormattedFileWithWrongFormattedBook() {
        var path = Paths.get("src", "test", "resources", "read_book_non_existing").toAbsolutePath();
        try {
            if(!Files.exists(Paths.get(path.toString(), "wrong_formatted.json"))) {
                Files.createFile(Paths.get(path.toString(), "wrong_formatted.json"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(var bw = Files.newBufferedWriter(Paths.get(path.toString(), "wrong_formatted"))) {
            bw.write("[\": \"123456789\",\"resume\": \"Je me baladais en foret\",\"author\": \"Cyril\"},");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final DataRepository jr = new JsonRepository(path, "wrong_formatted");
        List<BookDTO> expected = new ArrayList<>();
        var actual = jr.getData();
        for(var b : expected) {
            actual.contains(b);
        }
        deleteFile(Paths.get(path.toString(), "wrong_formatted.json"));
        assertIterableEquals(expected, actual);
    }

    @Test
    public void getDataFromFileWithMissingAttributes() {
        var path = Paths.get("src", "test", "resources", "read_book_non_existing").toAbsolutePath();
        try {
            if(!Files.exists(Paths.get(path.toString(), "wrong_formatted.json"))) {
                Files.createFile(Paths.get(path.toString(), "wrong_formatted.json"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var expected = new ArrayList<>(List.of(
                new BookDTO("", "123456789", "Cyril", "", "", "")
        ));
        try(var bw = Files.newBufferedWriter(Paths.get(path.toString(), "wrong_formatted.json"))) {
            bw.write("[{\"isbn\":\"123456789\",\"author\":\"Cyril\"}]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final DataRepository jr = new JsonRepository(path, "wrong_formatted");
        var actual = jr.getData();
        for(var b : expected) {
            actual.contains(b);
        }
        deleteFile(Paths.get(path.toString(), "wrong_formatted.json"));
        assertIterableEquals(expected, actual);
    }

    @Test
    public void getDataFromNotExistingFile() {
        var path = Paths.get("src", "test", "resources", "read_book_non_existing").toAbsolutePath();
        final DataRepository jr = new JsonRepository(path, "e200106");
        List<BookDTO> expected = new ArrayList<>(List.of());
        assertIterableEquals(expected, jr.getData());
        deleteFile(Paths.get(path.toString(), "e200106.json"));
    }

    @Test
    public void addDataToNotExistingRepoAndFile() {
        var path = Paths.get("src", "test", "resources", "write_book_non_existing").toAbsolutePath();
        final DataRepository jr = new JsonRepository(path, "e200106");
        List<Book> expected = new ArrayList<>(List.of());
        assertEquals(expected.size(), jr.getBooks().size());
        assertDoesNotThrow(() -> jr.add(
                new Book(new BookMetadata("Moi 3", "123458889", "Volume 3", "Audric"), "")));
        deleteFile(Paths.get(path.toString(), "e200106.json"));
        deleteFile(path);
    }

    @Test
    public void addInExistingFileAndEmpty() {
        final var newPath = Paths.get("src", "test", "resources", "write_inexisting_file");
        final DataRepository jr = new JsonRepository(newPath, "e200106");
        try {
            if(!Files.exists(newPath)) {
                Files.createFile(newPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var path = Paths.get("src", "test", "resources", "write_inexisting_file").toAbsolutePath();
        List<Book> expected = new ArrayList<>(List.of(
                new Book(new BookMetadata("Moi 3", "123458889", "Volume 3", "Audric"), "")
        ));
        assertDoesNotThrow(() -> jr.add(
                new Book(new BookMetadata("Moi 3", "123458889", "Volume 3", "Audric"), "")));
        var temp = jr.getData();
        List<Book> actual = new ArrayList<>();
        temp.forEach(e -> actual.add(Mapping.convertToBook(e)));
        deleteFile(Paths.get(path.toString(), "e200106.json"));
        for(var b : expected) {
            actual.contains(b);
        }
    }

    @Test
    public void addInExistingFileAndNotEmpty() {
        try {
            if(!Files.exists(Paths.get("src", "test", "resources", "write_inexisting_file", "e200106.json"))) {
                Files.createFile(Paths.get("src", "test", "resources", "write_inexisting_file", "e200106.json"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var path = Paths.get("src", "test", "resources", "write_inexisting_file").toAbsolutePath();
        final DataRepository jr = new JsonRepository(path, "e200106");
        List<Book> expected = new ArrayList<>(List.of(
                new Book(new BookMetadata("Moi 2", "123458889", "Volume 3", "Audric")),
                new Book(new BookMetadata("Moi 3", "123457289", "Volume 3", "Audric"))
        ));
        assertDoesNotThrow(() -> jr.add(
                new Book(new BookMetadata("Moi 2", "123458889", "Volume 3", "Audric"))));
        assertDoesNotThrow(() -> jr.add(
                new Book(new BookMetadata("Moi 3", "123457289", "Volume 3", "Audric"))));
        var temp = jr.getData();
        List<Book> actual = new ArrayList<>();
        temp.forEach(e -> actual.add(
                new Book(new BookMetadata(e.getTitle(), e.getResume(), e.getAuthor(), e.getIsbn()), "")
        ));
        deleteFile(Paths.get(path.toString(), "e200106.json"));
        assertEquals(expected.size(), actual.size());
        for(var b : expected) {
            actual.contains(b);
        }
    }

    private void deleteFile(final Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void hasSameDate(List<Book> it1, List<Book> it2) {
        for(final var b : it1) {
            assertTrue(it2.contains(b));
        }
    }

    private void contains(final Book elmnt, final List<Book> to) {
        assertTrue(to.contains(elmnt));
    }
}