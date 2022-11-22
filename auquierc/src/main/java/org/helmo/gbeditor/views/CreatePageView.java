package org.helmo.gbeditor.views;

import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import org.helmo.gbeditor.presenter.CreatePageInterface;
import org.helmo.gbeditor.presenter.CreatePagePresenter;
import org.helmo.gbeditor.presenter.ViewName;
import org.helmo.gbeditor.views.style.Theme;

import java.util.Collection;

public class CreatePageView extends View implements CreatePageInterface {

    private final CreatePagePresenter presenter;

    private final HeaderView header = new HeaderView("Créer une nouvelle page", "Home", Theme.SND_WINDOW_WIDTH); {
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

    private final Button addChoiceBtn = new Button("Ajouter un choix"); {
        addChoiceBtn.setOnAction(a -> notifyOnNewChoice());
    }

    private final ComboBox<String> addOptions = new ComboBox<>(); {
        addOptions.setOnAction(a -> notifyOptionSelected());
    }

    private final FlowPane optionPnl = new FlowPane(); {
        optionPnl.getChildren().addAll(new Label("Où ajouter la page?"), addOptions);
    }

    private final ComboBox<String> choiceTarget = new ComboBox<>();

    private final TextArea choiceContent = new TextArea();

    private final Button validChoiceBtn = new Button("Ajouter le choix"); {
        validChoiceBtn.setOnAction(a -> onAddNewChoice());
    }

    private final Button showChoicesBtn = new Button("Ajouter un choix"); {
        showChoicesBtn.setOnAction(a -> onNotifyShowChoices());
    }

    private final FlowPane choiceForm = new FlowPane(); {
        choiceForm.getChildren().addAll(
                new Label("Nouveau choix"),
                new VBox(new Label("Intitulé: "), choiceContent),
                new VBox(new Label("Page cible: "), choiceTarget),
                validChoiceBtn);
    }

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
                showChoicesBtn,
                choiceForm,
                validPage);

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

    private void notifyOnNewChoice() {
        presenter.onNotifyNewChoice();
    }

    private void onAddNewChoice() {
        presenter.onNotifyNewChoice(choiceContent.getText(),
            choiceTarget.getSelectionModel().getSelectedItem());
    }

    private void notifyOptionSelected() {
        presenter.onNotifyOptionSelected(addOptions.getSelectionModel().getSelectedItem());
    }

    private void onNotifyShowChoices() {
        presenter.onShowChoices();
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

    }

    @Override
    public void showBookPages(boolean show) {
        bookPages.setVisible(show);
    }

    @Override
    public void showChoiceForm() {
        choiceForm.setVisible(true);
    }

    @Override
    public void hideChoiceForm() {
        choiceForm.setVisible(false);
    }

    @Override
    public void addChoiceTarget(String choice) {
        choiceTarget.getItems().add(choice);
    }

    @Override
    public void clearChoiceTarget() {
        choiceTarget.getItems().clear();
    }

    @Override
    public void refresh() {
        presenter.onRefresh();
    }
}
