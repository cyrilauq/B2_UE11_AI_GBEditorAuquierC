package org.helmo.gbeditor.views;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.helmo.gbeditor.presenter.ViewName;
import org.helmo.gbeditor.views.style.Theme;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe s'occupe de l'affichage des différentes vues et des changements de vue.
 *
 * @author  Cyril Auquier
 */
public class MainWindow extends Scene {
    private final Map<String, View> views = new HashMap<>();
    private final Popup popup = new Popup();
    private View currentView;
    private Stage stage;
    private Stage sndStage;

    /**
     * Crée une nouvelle MainWindow avec un Pane et des vues données.
     *
     * @param root      Conteneur de départ.
     * @param views     Vues que l'application devra utiliser.
     */
    public MainWindow(final Pane root, final View... views) {
        super(root, Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
        getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        initViews(views);
    }

    private void initViews(final View... views) {
        for(final var v : views) {
            // TODO : se documenter sur les références de méthodes.
            currentView = v;
            v.setRouter(this::goTo);
            v.setShowPopUp(this::setSndView);
            v.setRefresh(this::refreshAll);
            this.views.put(v.getTitle(), v);
        }
    }

    private void initSndStage() {
        sndStage = new Stage();
        sndStage.setScene(new Scene(new VBox(), Theme.WINDOW_WIDTH * .7, Theme.WINDOW_HEIGHT));
        sndStage.initOwner(stage);
        sndStage.getScene().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        sndStage.initModality(Modality.WINDOW_MODAL);
        sndStage.setResizable(false);
        sndStage.setX(105 + Theme.WINDOW_WIDTH);
        sndStage.setY(100);
    }

    private void setSndView(final String viewName) {
        var found = foundView(viewName);
        if(found != null) {
            sndStage.getScene().setRoot(found);
            found.onEnter(currentView == null ? "" : currentView.getTitle());
            sndStage.show();
        }
    }

    /**
     * Permet de voyager d'une vue à une autre.
     *
     * @param viewName  Vue vers laquelle on veut aller.
     *
     * Source: AI bloc 1 2022 et 2021.
     */
    private void goTo(final String viewName) {
        var found = foundView(viewName);

        found.onEnter(currentView.getTitle());
        currentView.onLeave(found.getTitle());
        setRoot(currentView = found);
    }

    private View foundView(final String viewName) {
        var found = views.get(viewName);
        if(found == null) {
            throw new IllegalArgumentException("La fenêtre " + viewName + " n'existe pas.");
        }
        return found;
    }

    /**
     * Démarre l'application avec la vue donnée.
     *
     * @param viewName    Vue à afficher au démarrage de l'application.
     */
    public void start(final ViewName viewName) {
        goTo(viewName.getName());
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
        for(var v : views.keySet()) {
            views.get(v).setStage(stage);
        }
        initSndStage();
    }

    private void refreshAll(final String fromView) {
        views.keySet().forEach(v -> views.get(v).refresh());
    }
}
