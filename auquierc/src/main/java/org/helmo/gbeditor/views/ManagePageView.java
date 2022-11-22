package org.helmo.gbeditor.views;

import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import org.helmo.gbeditor.presenter.ManagePageInterface;
import org.helmo.gbeditor.presenter.ManagePagePresenter;
import org.helmo.gbeditor.presenter.ViewName;

public class ManagePageView extends View implements ManagePageInterface {

    private final ManagePagePresenter presenter;

    private final Button newPageBtn = new Button("Créer page"); {
        newPageBtn.setOnAction(e -> notifyOnNewPagePressed());
    }

    private final HeaderView header = new HeaderView("Gestion des pages", "Home"); {
        header.add(newPageBtn, 1, 1);
    }

    private final FlowPane allPages = new FlowPane();

    /**
     * Crée une nouvelle Vuew avec un titre donné.
     *
     * @param viewName Titre de la vue.
     */
    public ManagePageView(final ViewName viewName, final ManagePagePresenter presenter) {
        super(viewName);

        // TODO : Refresh les pages lorsqu'une nouvelle page est créée ou qu'une page est modifiée.

        getChildren().addAll(header, allPages);
        header.setOnBtnAction(e -> presenter.onHomePressed());

        this.presenter = presenter;
        this.presenter.setView(this);
    }

    private void notifyOnNewPagePressed() {
        presenter.onNewPagePressed();
    }

    @Override
    public void onEnter(String fromView) {
        presenter.onEnter(fromView);
    }

    @Override
    public void addPage(int numPage, String content) {
        allPages.getChildren().add(new ListItemPageView(numPage, content, presenter));
    }

    @Override
    public void clearPages() {
        allPages.getChildren().clear();
    }
}
