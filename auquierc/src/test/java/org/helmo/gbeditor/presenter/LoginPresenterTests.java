package org.helmo.gbeditor.presenter;

import static org.mockito.Mockito.*;

import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.RepositoryFactory;
import org.helmo.gbeditor.repositories.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginPresenterTests {
    private ViewInterface mockedView;
    private DataRepository mockedRepo;
    private LoginPresenter presenter;
    private Session session;

    @BeforeEach
    public void setUp() {
        mockedView = mock(ViewInterface.class);
        mockedRepo = mock(DataRepository.class);
        presenter = new LoginPresenter(session = mock(Session.class), new RepositoryFactory(true).newRepository());
    }

    @Test
    void connectionHasValidInfoThenPresenterCallsGoTo() {
        presenter.setView(mockedView);
        presenter.onConnexion("Auquier", "Cyril");
        verify(mockedView, times(1)).goTo("HomeView");
    }

    @Test
    void onConnexionWithNullNameThenDisplayMessage() {
        presenter.setView(mockedView);
        presenter.onConnexion(null, "Cyril");
        verify(mockedView, times(1)).setMessage("Le nom ne peut pas être vide.", TypeMessage.MESSAGE);
        verify(mockedView, times(0)).goTo("CreateBookView");
        verify(mockedRepo, times(0)).loadBooks();
    }

    @Test
    void onConnexionWithEmptyNameThenDisplayMessage() {
        presenter.setView(mockedView);
        presenter.onConnexion("", "Cyril");
        verify(mockedView, times(1)).setMessage("Le nom ne peut pas être vide.", TypeMessage.MESSAGE);
        verify(mockedView, times(0)).goTo("CreateBookView");
        verify(mockedRepo, times(0)).loadBooks();
    }

    @Test
    void onConnexionWithEmptyFirstNameThenDisplayMessage() {
        presenter.setView(mockedView);
        presenter.onConnexion("Auquier", "");
        verify(mockedView, times(1)).setMessage("Le prénom ne peut pas être vide.", TypeMessage.MESSAGE);
        verify(mockedView, times(0)).goTo("CreateBookView");
        verify(mockedRepo, times(0)).loadBooks();
    }

    @Test
    void onConnexionWithNullFirstNameThenDisplayMessage() {
        presenter.setView(mockedView);
        presenter.onConnexion("Auquier", null);
        verify(mockedView, times(1)).setMessage("Le prénom ne peut pas être vide.", TypeMessage.MESSAGE);
        verify(mockedView, times(0)).goTo("CreateBookView");
        verify(mockedRepo, times(0)).loadBooks();
    }

    @Test
    void onConnexionWithNullFormContentThenDisplayMessage() {
        presenter.setView(mockedView);
        presenter.onConnexion(null, null);
        verify(mockedView, times(1)).setMessage("Vous devez compléter le formulaire pour vous connecter.", TypeMessage.MESSAGE);
        verify(mockedView, times(0)).goTo("CreateBookView");
        verify(mockedRepo, times(0)).loadBooks();
    }
}
