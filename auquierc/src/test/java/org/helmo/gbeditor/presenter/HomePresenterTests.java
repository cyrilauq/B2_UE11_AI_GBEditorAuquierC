package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.JsonRepository;
import org.helmo.gbeditor.infrastructures.RepositoryFactory;
import org.helmo.gbeditor.modeles.ExtendedBookDescription;
import org.helmo.gbeditor.modeles.LittleBookDescription;
import org.helmo.gbeditor.repositories.DataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class HomePresenterTests {
    private HomeInterface mockedView;
    private DataRepository repo;
    private HomePresenter presenter;
    private Session session;
    private final Path path = Paths.get("src", "test", "resources", "HomeViewTests").toAbsolutePath();
    private final static String FILE_NAME = "tests";

    @BeforeEach
    void setUp() {
        mockedView = mock(HomeInterface.class);
        repo = new JsonRepository(path, FILE_NAME);
        session = new Session();
        session.setAuthor("Auquier", "Cyril");
        presenter = new HomePresenter(session, repo);
        presenter.setView(mockedView);
    }

    @AfterEach
    void setDown() {
        deleteFile(Paths.get(path.toString(), FILE_NAME + ".json"));
        deleteFile(path);
    }

    @Test
    void onSetViewConstructView() {
        presenter.onEnter("LoginView");
        verify(mockedView, times(1)).clearBooks();
        verify(mockedView, times(1)).setCurrentPage(1);
        verify(mockedView, times(1)).setAuthorName(session.getAuthor());
    }

    @Test
    void onCreateBookBtnPressedThenGoToCreateBookView() {
        presenter.onCreate();
        verify(mockedView, times(1)).showPopUp("CreateBookView");
    }

    @Test
    void onEnterInHomeViewWith0BookInFileThenAddBookIsNotCalled() {
        presenter.onEnter("CreateView");
        verify(mockedView, times(0)).addBook(null);
    }

    @Test
    void onEnterInHomeViewWith4BookInFileThenAddBookIsCalled4Times() {
        session = new Session();
        session.setAuthor("Auquier", "Cyril");
        repo.setCurrentAuthor("Auquier Cyril");
        final var factory = RepositoryFactory.get(
                Paths.get(
                        "src", "test", "resources", "PresenterTests"
                ).toString(), "all_same_books", "Auquier Cyril"
        );
        factory.setCurrentAuthor("Auquier Cyril");
        presenter = new HomePresenter(session, factory.newRepository());
        presenter.setView(mockedView);
        clearInvocations(mockedView);
        presenter.onEnter("CreateView");
        verify(mockedView, times(4))
                .addBook(new LittleBookDescription("Moi", "", "0-123456-78-9", "Auquier Cyril"));
    }

    @Test
    void userSelectedOneBookThenDisplayHisDetails() {
        session = new Session();
        session.setAuthor("Auquier", "Cyril");
        final var factory = RepositoryFactory.get(
                Paths.get(
                        "src", "test", "resources", "PresenterTests"
                ).toString(), "all_same_books", "Auquier Cyril"
        );
        factory.setCurrentAuthor("Auquier Cyril");
        presenter = new HomePresenter(session, factory.newRepository());
        presenter.setView(mockedView);
        presenter.onEnter("CreateView");
        presenter.displayDetailsFor("0123456789");
        verify(mockedView, atLeastOnce())
                .setDetails(new ExtendedBookDescription(
                        new LittleBookDescription("Moi", "", "0-123456-78-9", "Auquier Cyril"),
                        "Je me baladais en foret")
                );
    }

    private void deleteFile(final Path path) {
        try {
            if(Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
