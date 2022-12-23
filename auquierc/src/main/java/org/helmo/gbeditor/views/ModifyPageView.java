package org.helmo.gbeditor.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.helmo.gbeditor.presenter.viewmodels.ListChoiceItem;
import org.helmo.gbeditor.presenter.ModifyPageInterface;
import org.helmo.gbeditor.presenter.ModifyPagePresenter;
import org.helmo.gbeditor.presenter.TypeMessage;
import org.helmo.gbeditor.presenter.ViewName;

public class ModifyPageView extends View implements ModifyPageInterface {
    private final ModifyPagePresenter presenter;

    private final HeaderView header = new HeaderView("Modification de page", "Retour"); {
        header.setOnBtnAction(e -> notifyOnHomePressed());
    }

    private final ComboBox<String> addOptions = new ComboBox<>();

    private final FlowPane optionPnl = new FlowPane(); {
        optionPnl.getChildren().addAll(new Label("Où ajouter la page?"), addOptions);
    }

    private final ComboBox<ListItemPageView> choiceTarget = new ComboBox<>();

    private final TextArea choiceContent = new TextArea();

    private final Button validChoiceBtn = new Button("Ajouter le choix"); {
        validChoiceBtn.setOnAction(e -> notifyOnChoiceCreated());
    }

    private final Button showChoicesBtn = new Button("Ajouter un choix");

    private final VBox choiceForm = new VBox(); {
        choiceForm.getChildren().addAll(
                new Label("Nouveau choix"),
                new VBox(new Label("Intitulé: "), choiceContent),
                new VBox(new Label("Page cible: "), choiceTarget),
                validChoiceBtn);
    }

    private final ObservableList<ChoiceView> tableItems = FXCollections.observableArrayList();

    private final FlowPane choices = new FlowPane();

    private final Label message = new Label();

    private final VBox choicesPnl = new VBox(new Label("Choix disponibles"), choices);

    /**
     * Crée une nouvelle Vue avec un titre et un presenter donné.
     *
     * @param viewName Titre de la vue.
     */
    public ModifyPageView(ViewName viewName, final ModifyPagePresenter presenter) {
        super(viewName);

        getChildren().addAll(header, optionPnl, choicesPnl, choiceForm, message);

        this.presenter = presenter;
        presenter.setView(this);
    }

    private void notifyOnChoiceCreated() {
        presenter.onChoiceCreated(choiceContent.getText(), choiceTarget.getSelectionModel().getSelectedItem() == null ? null :  choiceTarget.getSelectionModel().getSelectedItem().getItemContent());
    }

    private void notifyOnHomePressed() {
        presenter.onHomePressed();
    }

    @Override
    public void onEnter(String fromView) {
        presenter.onEnter(fromView);
    }

    @Override
    public void setPageContent(String content) {

    }

    @Override
    public void setMessage(String txt, TypeMessage type) {
        message.getStyleClass().clear();
        message.getStyleClass().add(type.name());
        message.setText(txt);
    }

    @Override
    public void setChoices(Iterable<ListChoiceItem> choices) {
        this.choices.getChildren().clear();
        for (final var c : choices) {
            this.choices.getChildren().add(new ChoiceView(c.getLabel(), c.getNumPage(), c.getContent(), presenter));
        }
    }

    @Override
    public void setTarget(Iterable<ListChoiceItem> choices) {
        this.choiceTarget.getItems().clear();
        for(final var c : choices) {
            this.choiceTarget.getItems().add(new ListItemPageView(c.getNumPage(), c.getContent()));
        }
    }
}
