package org.helmo.gbeditor.domains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BookTests {
    public Book book;

    @BeforeEach
    void setUp() {
        book = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106"
        );
    }

    @Test
    void aBookKnowHisRealIsbn() {
        assertEquals("2-200106-05-10", book.getIsbn());
    }

    @Test
    void aBookKnowHisTitle() {
        assertEquals("Title", book.getTitle());
    }

    @Test
    void aBookKnowHiResume() {
        assertEquals("Un test", book.getResume());
    }

    @Test
    void aBookKnowHiAuthor() {
        assertEquals("You", book.getAuthor());
    }

    @Test
    void whenCreateOnBookWithWrongIsbnThenThrowException() {
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata("Title","2-200106-05-5", "Un test", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata("Title","2-0-05-5", "Un test", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata("Title","", "Un test", "You"),
                        "200106"
                ));
        assertThrows(ISBN.WrongFormattedISBNException.class,
                () -> Book.of(
                        new BookMetadata("Title",null, "Un test", "You"),
                        "200106"
                ));
    }

    @Test
    void whenCreateOnBookWithWrongTitleThenThrowException() {
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata(null,"2-200106-05-5", "Un test", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata(null,"2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ));
    }

    @Test
    void whenCreateOnBookWithWrongResumeThenThrowException() {
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata("Title","2-200106-05-5", "", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata("Title","2-200106-05-5", null, "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> Book.of(
                        new BookMetadata("Title","2-200106-05-5", null, "You"),
                        "200106", "fileName.png"
                ));
    }

    @Test
    void whenCreateOnValidBookThenDoesNotThrowException() {
        assertDoesNotThrow(() ->
                Book.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106"
                ));
        assertDoesNotThrow(() ->
                Book.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ));
    }

    @Test
    void aBookObjetIsNeverEqualToAnObjetThatIsNotAnInstanceOfBookClass() {
        var book = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106", "fileName.png"
        );
        assertNotEquals(null, book);
        assertNotEquals("", book);
        assertNotEquals(25, book);
    }

    @Test
    void whenEqualsWithTwoBookWithSameTitleButDifferentIsbnThenFalse() {
        assertNotEquals(
                Book.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ),
                Book.of(
                        new BookMetadata("Title","2-200106-30-0", "Un test", "You"),
                        "200106", "fileName.png"
                )
        );
    }

    @Test
    void whenEqualsWithTwoBookWithSameIsbnButDifferentTitleThenTrue() {
        assertEquals(
                Book.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ),
                Book.of(
                        new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                )
        );
    }

    @Test
    void whenEqualsWithTwoIdenticalReferencesThenTrue() {
        var book = Book.of(
                new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"),
                "200106", "fileName.png"
        );
        assertEquals(book, book);
    }

    @Test
    void canComputeNumPageWith3GivenPages() {
        var page1 = new Page("Premier");
        var page2 = new Page("Deuxième");
        var page3 = new Page("Troisième");
        page1.setChoices(Map.of("Retour", page2));
        page2.setChoices(Map.of("Retour", page3));
        List<Page> pages = new ArrayList<>(List.of(
                page2, page3, page1
        ));
        var book = new Book(new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"));

        book.addBegin(page1);
        book.addEnd(page3);
        book.addAfter(page2, page1);

        assertEquals(1, book.getNPageFor(page1));
        assertEquals(2, book.getNPageFor(page2));
        assertEquals(3, book.getNPageFor(page3));

        assertIterableEquals(new ArrayList<>(
                        List.of(
                                page1, page2, page3)
                ),
                book
        );
    }

    @Test
    void canReComputeNumPageWhenPage2IsRemoved() {
        var page1 = new Page("Premier");
        var page2 = new Page("Deuxième");
        var page3 = new Page("Troisième");
        page1.setChoices(Map.of("Retour", page2));
        page2.setChoices(Map.of("Retour", page3));
        List<Page> pages = new ArrayList<>(List.of(
                page2, page3, page1
        ));
        var book = new Book(new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"));
        book.addBegin(page1);
        book.addEnd(page2);
        book.addAfter(page3, page2);
        book.removePage(page2);

        var resultPage1 = new Page("Premier");
        var resultPage3 = new Page("Troisième");

        assertEquals(1, book.getNForPage(page1));
        assertEquals(2, book.getNForPage(page3));
        assertIterableEquals(new ArrayList<>(
                        List.of(
                                resultPage1, resultPage3)
                ),
                book
        );
    }

    @Test
    void canDeleteAllThePages() {
        var page1 = new Page("Premier");
        var page2 = new Page("Deuxième");
        var page3 = new Page("Troisième");
        page1.setChoices(Map.of("Retour", page2));
        page2.setChoices(Map.of("Retour", page3));
        List<Page> pages = new ArrayList<>(List.of(
                page1, page2, page3
        ));
        var book = new Book(new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"), pages);
        book.removePage(page2);
        book.removePage(page3);
        book.removePage(page1);
        assertIterableEquals(new ArrayList<>(),
                book
        );
    }

    @Test
    void addOnePageToBook() {
        var book = Book.of(
                new BookMetadata("Test", "2-200106-05-X", "Test", "You You"),
                "200106",
                ""
        );
        var page1 = new Page("Page 1");
        book.addBegin(page1);
        var pages = List.of(page1);
        assertIterableEquals(book, pages);
    }

    @Test
    void addTwoPageToBook() {
        var book = Book.of(
                new BookMetadata("Test", "2-200106-05-X", "Test", "You You"),
                "200106",
                ""
        );
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        book.addBegin(page1);
        book.addEnd(page2);
        var pages = List.of(page1, page2);
        assertEquals(1, book.getNPageFor(page1));
        assertEquals(2, book.getNPageFor(page2));
        assertEquals(page2, book.getPageFor("Page 2"));
        assertEquals(2, book.pageCount());
        assertIterableEquals(book, pages);
    }

    @Test
    void whenPageDeleteThenAllReferenceInOtherPagesAreDeleted() {
        var book = Book.of(
                new BookMetadata("Test", "2-200106-05-X", "Test", "You You"),
                "200106",
                ""
        );
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        var page3 = new Page("Page 3");
        page1.setChoices(Map.of("Page 2", page2));
        page2.setChoices(Map.of("Page 3", page3));
        book.addEnd(page1);
        book.addEnd(page2);
        book.addEnd(page3);
        book.removePage(page2);
        assertEquals(1, book.getNPageFor(page1));
        assertEquals(2, book.getNPageFor(page3));
        assertEquals(page3, book.getPageFor("Page 3"));
        assertEquals(2, book.pageCount());
        assertFalse(page1.getChoices().values().contains(page2));
        assertFalse(page3.getChoices().values().contains(page2));
    }

    @Test
    void whenChangeMetaDataOfTheBookThenTheChangedBookNoMoreEqualsTheAncientOne() {
        var changedBook = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106"
        );
        var expected = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "You"),
                "200106"
        );
        assertEquals(changedBook, book);
        changedBook.setNewData(new BookMetadata(
                "Title",
                "2-200106-30-0",
                "Un test",
                "You"
        ), "200106", "");
        assertNotEquals(changedBook, book);
        assertEquals(expected, changedBook);
    }

    @Test
    void whenChangeDataOfBookWithInvalidInformationThenThrowException() {
        var book1 = Book.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106"
        );
        var book2 = Book.of(
                new BookMetadata("Title","2-200106-30-0", "Un test", "You"),
                "200106"
        );
        assertThrows(Book.WrongFormattedBookException.class, () -> book1.setNewData(
                new BookMetadata(
                        "", "2-200106-05-X", "Un test", "You"
                ),
                "200106",
                ""));
        assertThrows(Book.WrongFormattedBookException.class, () -> book1.setNewData(
                new BookMetadata(
                        "Test", "2-200106-05-3", "Un test", "You"
                ),
                "200106",
                ""));
    }

    @Test
    void whenBookIsPublishedThenModifyItIsImpossible() {
        book.publish();
        assertThrows(Book.CannotModifyPublishedBookException.class, () -> book.setNewData(
                new BookMetadata(
                        "Test", "2-200106-05-3", "Un test", "You"
                ),
                "200106",
                ""));
    }

    @Test
    void whenPublishBookAlreadyPublishedThenThrowException() {
        book.publish();
        assertThrows(Book.BookAlreadyPublishedException.class, () -> book.publish());
    }
}
