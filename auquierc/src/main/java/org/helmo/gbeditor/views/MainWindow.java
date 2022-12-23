package org.helmo.gbeditor.views;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
    private View currentView;
    private final Pane mainView = new Pane(new Pane());
    private final Pane sndView = new Pane(new Pane());

    /**
     * Crée une nouvelle MainWindow avec un Pane et des vues données.
     *
     * @param views     Vues que l'application devra utiliser.
     */
    public MainWindow(final View... views) {
        super(new Pane(), Theme.WINDOW_WIDTH * 2, Theme.WINDOW_HEIGHT);
        setRoot(new HBox(mainView, sndView));
        getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        initViews(views);
    }

    private void initViews(final View... views) {
        for(final var v : views) {
            currentView = v;
            v.setRouter(this::goTo);
            v.setShowPopUp(this::setSndView);
            v.setRefresh(this::refreshAll);
            this.views.put(v.getTitle(), v);
        }
    }

    private void setSndView(final String viewName) {
        var found = foundView(viewName);
        found.onEnter(viewName);
        sndView.getChildren().set(0, found);
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
        mainView.getChildren().set(0, currentView = found);
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

    private void refreshAll(final String fromView) {
        views.keySet().forEach(v -> views.get(v).refresh());
    }
}
