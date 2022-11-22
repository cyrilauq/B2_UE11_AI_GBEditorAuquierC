package org.helmo.gbeditor;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.RepositoryFactory;
import org.helmo.gbeditor.presenter.*;
import org.helmo.gbeditor.views.*;

/**
 * Classe principale s'occupant du lancement du programme.
 */
public class Program extends Application {

    /**
     * Lance le programme.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainWindow mainWindow = getMainWindow();
        mainWindow.start(ViewName.LOGIN_VIEW);

        stage.setTitle("AI - Modification et écriture de livre-jeu.");
        stage.setResizable(false);
        stage.setX(100);
        stage.setY(100);

        stage.setScene(mainWindow);
        mainWindow.setStage(stage);

        stage.show();
    }

    /**
     * Instancie une nouvelle MainWindow
     *
     * @return  Retourne une nouvelle MainWindow avec une Session et un Repository.
     */
    private static MainWindow getMainWindow() {
        var repo = new RepositoryFactory(false).newRepository();
        var session = new Session();
        return new MainWindow(new Pane(),
                new LoginView(ViewName.LOGIN_VIEW, new LoginPresenter(session, repo)),
                new CreateBookView(ViewName.CREATE_BOOK_VIEW, new CreateBookPresenter(session, repo)),
                new ModifyBookView(ViewName.MODIFY_BOOK_VIEW, new ModifyBookPresenter(session, repo)),
                new HomeView(ViewName.HOME_VIEW, new HomePresenter(session, repo)),
                new CreatePageView(ViewName.CREATE_PAGE_VIEW, new CreatePagePresenter(session, repo)),
                new ManagePageView(ViewName.MANAGA_PAGE_VIEW, new ManagePagePresenter(session, repo)),
                new ModifyPageView(ViewName.MODIFY_PAGE_VIEW, new ModifyPagePresenter(session, repo))
        );
    }
}
