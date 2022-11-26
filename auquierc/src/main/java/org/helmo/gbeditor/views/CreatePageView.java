package org.helmo.gbeditor.views;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.helmo.gbeditor.presenter.CreatePageInterface;
import org.helmo.gbeditor.presenter.CreatePagePresenter;
import org.helmo.gbeditor.presenter.TypeMessage;
import org.helmo.gbeditor.presenter.ViewName;
import org.helmo.gbeditor.views.style.Theme;

import java.util.Collection;

public class CreatePageView extends View implements CreatePageInterface {

    private final CreatePagePresenter presenter;

    private final HeaderView header = new HeaderView("Créer une nouvelle page", "Retour", Theme.SND_WINDOW_WIDTH); {
        header.setOnBtnAction(a -> notifyOnHomePressed());
    }

    private final TextArea pageContent = new TextArea(); {
        pageContent.setWrapText(true);
        pageContent.addEventHandler(KeyEvent.KEY_TYPED, new KeyTypesHandler(pageContent, null, 150));
        pageContent.setPrefWidth(Theme.SND_WINDOW_WIDTH * .5);
    }

    private final Label pageContentLbl = new Label("Contenu de la page"); {
        pageContentLbl.setWrapText(true);
    }

    private final HBox pageContentPnl = new HBox(); {
        pageContentPnl.getChildren().addAll(new Label("Contenu de la page"),
                pageContent);
    }

    private final ComboBox<ListItemPageView> bookPages = new ComboBox<>();

    private final Button validPage = new Button("Ajouter la page"); {
        validPage.setOnAction(a -> notifyOnPageCreated());
    }

    private final ComboBox<String> addOptions = new ComboBox<>(); {
        addOptions.setOnAction(a -> notifyOptionSelected());
    }

    private final FlowPane optionPnl = new FlowPane(); {
        optionPnl.getChildren().addAll(new Label("Où ajouter la page?"), addOptions);
    }

    private final Label message = new Label();

    /**
     * Crée une nouvelle CreatePageView avec un titre donné et le presenter correspondant à la vue.
     *
     * @param viewName  Titre de la vue.
     * @param presenter Presenter qui s'occupe de la logique métier liéée à la vue.
     */
    public CreatePageView(final ViewName viewName, final CreatePagePresenter presenter) {
        super(viewName);

        getChildren().addAll(header,
                pageContentPnl,
                optionPnl,
                bookPages,
                validPage,
                message);

        this.presenter = presenter;
        this.presenter.setView(this);
    }

    public void notifyOnHomePressed() {
        presenter.onHomePressed();
    }

    public void notifyOnPageCreated() {
        presenter.onNotifyAddPage(pageContent.getText(),
                bookPages.getItems().size() > 0 && bookPages.getSelectionModel().getSelectedIndex() > -1 ? bookPages.getSelectionModel().getSelectedItem().getItemContent() : null,
                getSelectedOption());
    }

    private String getSelectedOption() {
        return addOptions.getSelectionModel().getSelectedItem();
    }

    private void notifyOptionSelected() {
        presenter.onNotifyOptionSelected(addOptions.getSelectionModel().getSelectedItem());
    }

    @Override
    public void onEnter(String fromView) {
        presenter.onEnter(fromView);
    }

    @Override
    public void addBookPages(final int numPage, final String pageContent) {
        bookPages.getItems().add(
                new ListItemPageView(numPage, pageContent)
        );
    }

    @Override
    public void clearBookPages() {
        bookPages.getItems().clear();
    }

    @Override
    public void setAddOptions(Collection<String> addOptions, final String selectedOption) {
        this.addOptions.getItems().clear();
        addOptions.forEach(a -> this.addOptions.getItems().add(a));
        this.addOptions.getSelectionModel().select(selectedOption);
    }

    @Override
    public void setMessage(String txt) {
        message.setText(txt);
    }

    @Override
    public void setMessage(String txt, TypeMessage message) {
        this.message.setText(txt);
    }

    @Override
    public void showBookPages(boolean show) {
        bookPages.setVisible(show);
    }

    @Override
    public void refresh() {
        presenter.onRefresh();
    }
}
