package org.helmo.gbeditor.domains;

import org.helmo.gbeditor.factory.BookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BookTests {
    public Book book;

    @BeforeEach
    void setUp() {
        book = BookFactory.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106"
        );
    }

    @Test
    void aBookKnowHisTitle() {
        assertEquals("Title", book.get(BookFieldName.TITLE));
    }

    @Test
    void aBookKnowHiResume() {
        assertEquals("Un test", book.get(BookFieldName.SUMMARY));
    }

    @Test
    void aBookKnowHiAuthor() {
        assertEquals("You", book.get(BookFieldName.AUTHOR));
    }

    @Test
    void whenCreateOnBookWithWrongIsbnThenThrowException() {
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title","2-200106-05-5", "Un test", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title","2-0-05-5", "Un test", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title","", "Un test", "You"),
                        "200106"
                ));
        assertThrows(ISBN.WrongFormattedISBNException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title",null, "Un test", "You"),
                        "200106"
                ));
    }

    @Test
    void whenCreateOnBookWithWrongTitleThenThrowException() {
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata(null,"2-200106-05-5", "Un test", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata(null,"2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ));
    }

    @Test
    void whenCreateOnBookWithWrongResumeThenThrowException() {
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title","2-200106-05-5", "", "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title","2-200106-05-5", null, "You"),
                        "200106"
                ));
        assertThrows(Book.WrongFormattedBookException.class,
                () -> BookFactory.of(
                        new BookMetadata("Title","2-200106-05-5", null, "You"),
                        "200106", "fileName.png"
                ));
    }

    @Test
    void whenCreateOnValidBookThenDoesNotThrowException() {
        assertDoesNotThrow(() ->
                BookFactory.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106"
                ));
        assertDoesNotThrow(() ->
                BookFactory.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ));
    }

    @Test
    void aBookObjetIsNeverEqualToAnObjetThatIsNotAnInstanceOfBookClass() {
        var book = BookFactory.of(
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
                BookFactory.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ),
                BookFactory.of(
                        new BookMetadata("Title","2-200106-30-0", "Un test", "You"),
                        "200106", "fileName.png"
                )
        );
    }

    @Test
    void whenEqualsWithTwoBookWithSameIsbnButDifferentTitleThenTrue() {
        assertEquals(
                BookFactory.of(
                        new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                ),
                BookFactory.of(
                        new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"),
                        "200106", "fileName.png"
                )
        );
    }

    @Test
    void whenEqualsWithTwoIdenticalReferencesThenTrue() {
        var book = BookFactory.of(
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
        var book = new Book(new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"));

        book.addBegin(page1);
        book.addEnd(page3);
        book.addAfter(page2, page1);

        assertEquals(1, book.getNForPage(page1));
        assertEquals(2, book.getNForPage(page2));
        assertEquals(3, book.getNForPage(page3));

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
        var book = new Book(new BookMetadata("kmlkmlk","2-200106-05-X", "Un test", "You"));
        book.addEnd(page1);
        book.addEnd(page2);
        book.addEnd(page3);
        book.removePage(page2);
        book.removePage(page3);
        book.removePage(page1);
        assertIterableEquals(new ArrayList<>(),
                book
        );
    }

    @Test
    void addOnePageToBook() {
        var book = BookFactory.of(
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
        var book = BookFactory.of(
                new BookMetadata("Test", "2-200106-05-X", "Test", "You You"),
                "200106",
                ""
        );
        var page1 = new Page("Page 1");
        var page2 = new Page("Page 2");
        book.addBegin(page1);
        book.addEnd(page2);
        var pages = List.of(page1, page2);
        assertEquals(1, book.getNForPage(page1));
        assertEquals(2, book.getNForPage(page2));
        assertEquals(page2, book.getPageFor("Page 2"));
        assertIterableEquals(book, pages);
    }

    @Test
    void whenPageDeleteThenAllReferenceInOtherPagesAreDeleted() {
        var book = BookFactory.of(
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
        assertEquals(1, book.getNForPage(page1));
        assertEquals(2, book.getNForPage(page3));
        assertEquals(page3, book.getPageFor("Page 3"));
        assertFalse(page1.getChoices().values().contains(page2));
        assertFalse(page3.getChoices().values().contains(page2));
    }

    @Test
    void whenChangeMetaDataOfTheBookThenTheChangedBookNoMoreEqualsTheAncientOne() {
        var changedBook = BookFactory.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106"
        );
        var expected = BookFactory.of(
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
        var book1 = BookFactory.of(
                new BookMetadata("Title","2-200106-05-X", "Un test", "You"),
                "200106"
        );
        var book2 = BookFactory.of(
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
        book.addEnd(new Page("Page 1"));
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
        book.addEnd(new Page("Page 1"));
        book.publish();
        assertThrows(Book.BookAlreadyPublishedException.class, () -> book.publish());
    }
}
