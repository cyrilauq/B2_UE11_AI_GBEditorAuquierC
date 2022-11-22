package org.helmo.gbeditor.domains;

import org.helmo.gbeditor.factory.ISBNFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ISBNTests {
    @Test
    public void createIsbnWithLang2Mat200106With5BooksGive220010605X() {
        var isbn = ISBNFactory.computeISBNFor(2, "200106", 5);
        assertEquals("2-200106-05-10", isbn.toString());
        assertEquals("2-200106-05-X", isbn.forUser());
    }

    @Test
    public void createIsbnWithLang2Mat200106With4BooksGive2200106041() {
        assertEquals("2-200106-04-1", ISBNFactory.computeISBNFor(2, "200106", 4).toString());
    }

    @Test
    public void createIsbnWithLang2Mat200017With1BooksGive2200017017() {
        var isbn = ISBNFactory.computeISBNFor(2, "200017", 1);
        assertEquals("2-200017-01-4", isbn.toString());
        assertEquals("2-200017-01-4", isbn.forUser());
    }

    @Test
    public void wrongISBNFormat() {
        assertEquals(ISBN.ISBNTypeError.WRONG_CONTROL_NUMBER.getMessage(), ISBN.isValid("2200106737", "200106"));
    }

    @Test
    public void whenNullIsbnThenThrowException() {
        assertThrows(ISBN.WrongFormattedISBNException.class,
                () -> ISBN.of(null));
    }

    @Test
    public void createIsbnWrongWithAuthorMatricule() {
        assertEquals(ISBN.ISBNTypeError.WRONG_AUTHOR.getMessage(), ISBN.isValid("2200017014", "200106"));
    }

    @Test
    public void wrongLangCode() {
        assertEquals(ISBN.ISBNTypeError.WRONG_LANG_CODE.getMessage(), ISBN.isValid("5200106737", "200106"));
    }

    @Test
    public void isbnToLong() {
        assertEquals(ISBN.ISBNTypeError.TOO_MANY_CHARACTER.getMessage(), ISBN.isValid("220010673557", "200106"));
    }

    @Test
    public void isbnIsNull() {
        assertEquals(ISBN.ISBNTypeError.EMTY_ISBN.getMessage(), ISBN.isValid(null, "200106"));
    }

    @Test
    public void isbnCannotContainsOtherLetterThanX() {
        assertEquals(ISBN.ISBNTypeError.CONTAINS_LETTER.getMessage(), ISBN.isValid("220010673A", "200106"));
        assertEquals(ISBN.ISBNTypeError.CONTAINS_LETTER.getMessage(), ISBN.isValid("220010673m", "200106"));
    }

    @Test
    public void isbnCanContainsOtherLetterX() {
        assertNull(ISBN.isValid("220010605X", "200106"));
    }

    @Test
    public void isbnWithVerifNumberEquals11() {
        assertNull(ISBN.isValid("22001063011", "200106"));
        assertNull(ISBN.isValid("2200106300", "200106"));
        assertEquals("2-200106-30-11", ISBN.of("2200106300").toString());
        assertEquals("2-200106-30-0", ISBN.of("2200106300").forUser());
    }

    @Test
    public void isbnWithVerifNumberEquals11But10IsWrittenMeansInvalidISBN() {
        assertNotNull(ISBN.isValid("2-200106-30-10", "200106"));
    }
}