package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.ISBN;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.JsonRepository;
import org.helmo.gbeditor.infrastructures.RepositoryFactory;
import org.helmo.gbeditor.repositories.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class CreateBookPresenterTests {
    private CreateBookPresenter presenter;
    private CreateBookInterface mockedView;
    private DataRepository mockedRepo;
    private Session session;

    @BeforeEach
    public void setUp() {
        session = new Session();
        session.setAuthor("Auquier", "Cyril");
        mockedRepo = new JsonRepository(Paths.get("src", "test", "resources", "PresenterTests"), "no_books");
        presenter = new CreateBookPresenter(session, new RepositoryFactory(true).newRepository());
        mockedView = mock(CreateBookInterface.class);
        presenter.setView(mockedView);
    }

    @Test
    public void presenterOnEnterCallsSetAuthorName() {
        presenter.onEnter("LoginView");
        verify(mockedView, times(1)).setAuthorName(session.getAuthor());
    }

    @Test
    void createBookWitNullTitle() {
        presenter.createNewBook(null, "", "", "");
        verifyOneCallForSetMessage(Book.BookTypeError.EMPTY_TITLE.getMessage());
    }

    @Test
    void createBookWitEmptyTitle() {
        presenter.createNewBook("", "", "", "");
        verifyOneCallForSetMessage(Book.BookTypeError.EMPTY_TITLE.getMessage());
    }

    @Test
    void createBookWithNullResume() {
        presenter.createNewBook("Test", "", null, "");
        verifyOneCallForSetMessage(Book.BookTypeError.EMPTY_RESUME.getMessage());
    }

    @Test
    void createBookWithEmptyResume() {
        presenter.createNewBook("Test", "", "", "");
        verifyOneCallForSetMessage(Book.BookTypeError.EMPTY_RESUME.getMessage());
    }

    @Test
    void createBookWithTooLongTitle() {
        presenter.createNewBook("j".repeat(156), "", "fffff", "");
        verifyOneCallForSetMessage(Book.BookTypeError.TITLE_TOO_LONG.getMessage());
    }

    @Test
    void createBookWithTooLongResume() {
        presenter.createNewBook("Test", "", "f".repeat(550), "");
        verifyOneCallForSetMessage(Book.BookTypeError.RESUME_TOO_LONG.getMessage());
    }

    @Test
    void createBookWithWrongFormattedISBN() {
        presenter.createNewBook("Test", "", "Je suis un test", "");
        verifyOneCallForSetMessage(ISBN.ISBNTypeError.EMTY_ISBN.getMessage());
    }

    @Test
    public void createBookWithRightInformations() {
        presenter.createNewBook("Test", "220010605X", "Je suis un test", "");
        verifyOneCallForSetMessage("Le livre a bien été créé.");
        verify(mockedView, times(1)).resetInputs();
        verify(mockedView, times(1)).refreshAll(ViewName.CREATE_BOOK_VIEW);
    }

    @Test
    void whenHomeButtonPressedGoToHomeView() {
        presenter.onHomePressed();
        verify(mockedView, times(1)).goTo("HomeView");
    }

    private void verifyOneCallForSetMessage(String message) {
        verify(mockedView, times(1)).setMessage(message, TypeMessage.MESSAGE);
    }
}
