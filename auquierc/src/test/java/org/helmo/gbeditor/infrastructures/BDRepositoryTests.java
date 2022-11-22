package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.BookMetadata;
import org.helmo.gbeditor.domains.Page;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.exception.UnableToConnectException;
import org.helmo.gbeditor.infrastructures.exception.UnableToTearDownException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BDRepositoryTests {
    private Session session;
    private BDRepository repo = null;

    private Book book1;
    private Book book2;

    @BeforeEach
    public void setUp() throws Exception {
        book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        session = new Session();
        session.setAuthor("Auquier", "Cyril");
        try {
            repo = RepositoryFactory.of(
                    "org.apache.derby.jdbc.EmbeddedDriver",
                    "jdbc:derby:Test;create=true",
                    "",
                    "",
                    "Auquier Cyril");
            repo.setUp();
        } catch(UnableToTearDownException e) {
            System.err.println(e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        repo.tearDown();
   }

    @Test
    public void createBDRepositoryWithRightParamsValuesDoesNotThrowAnyKindOfError() {
        assertDoesNotThrow(() ->
                RepositoryFactory.of(
                        "org.apache.derby.jdbc.EmbeddedDriver",
                        "jdbc:derby:Test;create=true",
                        "",
                        ""));
    }

    @Test
    public void createBDRepositoryWrongParamsValuesThrowUnableToConnectException() {
        assertThrows(UnableToConnectException.class, () ->
                RepositoryFactory.of(
                        "org.apache.derby.jdbc.EmbeddedDriver",
                        "jdbc:derby:Tests",
                        "",
                        ""));
    }

    @Test
    public void whenBdIsEmptyThenGetDataReturnEmptyCollection() {
        assertTrue(repo.getBooks().isEmpty());
    }

    @Test
    public void whenBdContains2BookThenRepoGiveTwoBooksCorrectlyFormatted() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book1, book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
    }

    @Test
    public void whenBdContains2BookThenRepoCanRemoveAtLeastOneBook() {
        // TODO : Corriger le mapping pour pouvoir récupérer les pages dans le bon ordre
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book1, book2);
        book1.addBegin(new Page("Hello"));
        repo.save(book1);
        repo.remove(book2.getIsbn());
        assertIterableEquals(List.of(book1), repo.getBooks());
        assertIterableEquals(book1, repo.getBooks().get(0));
    }

    @Test
    public void whenDeleteABookThenCanReAddTheSameBookInTheDB() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );

        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book1, book2);
        repo.remove(book2.getIsbn());
        assertIterableEquals(List.of(book1), repo.getBooks());
        repo.add(book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
    }

    @Test
    public void whenBookIsAlreadyInDBThenModifyIt() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book1, book2);
        repo.remove(book2.getIsbn());
        book2 = Book.of(
                new BookMetadata("Title Book V2","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
    }

    @Test
    public void whenBookHasPagesThenRepoSaveTheBookAndItsPages() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        book1.addBegin(page1);
        book1.addAfter(page2, page1);
        book2.addBegin(page1);
        book2.addEnd(page2);
        repo.add(book1, book2);
        repo.remove(book2.getIsbn());
        book2 = Book.of(
                new BookMetadata("Title Book V2","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
    }

    @Test
    public void whenAddPageThatAlreadyExistsThenUpdatePage() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        book1.addEnd(page1);
        book1.addEnd(page2);
        repo.add(book1, book2);
        repo.remove(book2.getIsbn());
        book2 = Book.of(
                new BookMetadata("Title Book V2","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
        book2.addEnd(page2);
        repo.save(book1);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
    }

    @Test
    public void whenDeletePageThenDeletePageWithoutThrowingException() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        book1.addBegin(page1);
        book1.addEnd(page2);
        book2.addBegin(page1);
        book2.addEnd(page2);
        repo.add(book1, book2);
        repo.remove(book2.getIsbn());
        book2 = Book.of(
                new BookMetadata("Title Book V2","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
        book1.removePage(page2);
        repo.save(book1);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
    }

    @Test
    void whenAddPageWithChoicesThenReturnPageWithTheSameChoices() {
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        var page3 = new Page("Page 3");
        var page4 = new Page("Page 4");
        var page5 = new Page("Page 5");
        page1.addChoice("Go to page2", page2);
        page1.addChoice("Go to page3", page3);
        page2.addChoice("Go to page4", page4);
        page4.addChoice("Go to page5", page5);
        book1.addEnd(page1);
        book1.addEnd(page2);
        book1.addEnd(page3);
        book1.addEnd(page4);
        book1.addEnd(page5);
        repo.add(book1);
        assertIterableEquals(List.of(book1), repo.getBooks());
        var book1Iter = book1.iterator();
        var repo1Iter = repo.getBooks().get(0).iterator();
        assertIterableEquals(book1, repo.getBooks().get(0));
        while(book1Iter.hasNext() && repo1Iter.hasNext()) {
            assertIterableEquals(book1Iter.next(), repo1Iter.next());
        }
    }

    @Test
    void whenBookIsPublishedAndSavedThenTheBookIsMemorizedInTheDBWithPublishDateTime() {
        repo.add(book1);
        var currentDateTime = LocalDateTime.now();
        book1.publish();
        repo.save(book1);
        var result = repo.getBooks();
        assertIterableEquals(List.of(book1), result);
        assertEquals(currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm")), result.get(0).getPublishDate().format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm")));
    }

    @Test
    void whenISBNOfBookIsChangedAndBookSavedThenNoBooksAreAddedAndTheBookIsUpdatedInTheDB() {
        var bookResult = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.add(book1, book2);
        assertIterableEquals(List.of(book1, book2), repo.getBooks());
        book1.setNewData(
                new BookMetadata("Title","2-200106-30-0", "Un test", "Auquier Cyril"),
                "200106", "fileName.png"
        );
        repo.save(book1);
        assertIterableEquals(List.of(bookResult, book2), repo.getBooks());
    }
}
