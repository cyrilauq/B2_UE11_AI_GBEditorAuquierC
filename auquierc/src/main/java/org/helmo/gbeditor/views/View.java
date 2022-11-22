package org.helmo.gbeditor.views;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.helmo.gbeditor.presenter.TypeMessage;
import org.helmo.gbeditor.presenter.ViewInterface;
import org.helmo.gbeditor.presenter.ViewName;

import java.util.function.Consumer;

/**
 * Cette classe définit les actions de bases qu'une vue doit faire.
 */
public class View extends VBox implements ViewInterface {
    private Consumer<String> router;
    private Consumer<String> refresh;
    private Consumer<String> showPopUp;
    private final ViewName viewName;

    protected Stage stage;

    /**
     * Crée une nouvelle Vuew avec un titre donné.
     *
     * @param viewName     Titre de la vue.
     */
    public View(final ViewName viewName) {
        this.viewName = viewName;
    }

    public String getTitle() {
        return viewName.getName();
    }

    /**
     * Définit l'action qui sera réalisée lors de l'appel à la méthode goTo.
     *
     * @param router    Action à réaliser.
     */
    public void setRouter(final Consumer<String> router) {
        this.router = router;
    }

    public void setRefresh(Consumer<String> refresh) {
        this.refresh = refresh;
    }

    public void setStage(final Stage stage) { this.stage = stage; }

    public void setShowPopUp(Consumer<String> showPopUp) {
        this.showPopUp = showPopUp;
    }

    @Override
    public void goTo(final String toView) {
        router.accept(toView);
    }

    /**
     * Cette méthode sera redéfinie par les classes filles.
     * Elle permet de réaliser une opération lorsqu'une touche et pressée.
     *
     * @param keyCode   Code de la touche pressée.
     */
    public void onKeyPressed(int keyCode) { }

    /**
     * Rafraichit la vue.
     * Cette méthode sera à redéfinir dans les classes filles.
     */
    public void refresh() { }

    @Override
    public void refreshAll(final String fromView) {
        refresh.accept(fromView);
    }

    @Override
    public void refreshAll(ViewName viewName) {
        refresh.accept(viewName.getName());
    }

    @Override
    public void onEnter(final String fromView) { }

    @Override
    public void onLeave(String fromView) { }

    /**
     * Cette méthode sera implémentée par les classes filles.
     * Elle affiche un message à l'utilisateur.
     */
    @Override
    public void setMessage(final String txt, final TypeMessage message) { }


    /**
     * Cette méthode sera implémentée par les classes filles.
     * Elle affiche le nom de l'auteur actuellement connecté.
     */
    @Override
    public void setAuthorName(final String name) { }

    @Override
    public void showPopUp(String viewName) {
        showPopUp.accept(viewName);
    }

    @Override
    public void showErrorMessage(String error) {

    }
}
