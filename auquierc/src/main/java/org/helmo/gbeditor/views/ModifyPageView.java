package org.helmo.gbeditor.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.helmo.gbeditor.modeles.ListChoiceItem;
import org.helmo.gbeditor.presenter.ModifyPageInterface;
import org.helmo.gbeditor.presenter.ModifyPagePresenter;
import org.helmo.gbeditor.presenter.ViewName;

import java.util.ArrayList;

public class ModifyPageView extends View implements ModifyPageInterface {
    private final ModifyPagePresenter presenter;

    private final HeaderView header = new HeaderView("Modification de page", "Home"); {
        header.setOnBtnAction(e -> notifyOnHomePressed());
    }

//    private final TextArea pageContent = new TextArea(); {
//        pageContent.setWrapText(true);
//        pageContent.addEventHandler(KeyEvent.KEY_TYPED, new KeyTypesHandler(pageContent, null, 150));
//        pageContent.setPrefWidth(Theme.SND_WINDOW_WIDTH * .5);
//    }
//
//    private final Label pageContentLbl = new Label("Contenu de la page"); {
//        pageContentLbl.setWrapText(true);
//    }
//
//    private final HBox pageContentPnl = new HBox(); {
//        pageContentPnl.getChildren().addAll(new Label("Contenu de la page"),
//                pageContent);
//    }

    private final ComboBox<String> bookPages = new ComboBox<>();

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

    private final FlowPane choiceForm = new FlowPane(); {
        choiceForm.getChildren().addAll(
                new Label("Nouveau choix"),
                new VBox(new Label("Intitulé: "), choiceContent),
                new VBox(new Label("Page cible: "), choiceTarget),
                validChoiceBtn);
    }

    private final ObservableList<ChoiceView> tableItems = FXCollections.observableArrayList();

    private final FlowPane choices = new FlowPane();

    private final VBox choicesPnl = new VBox(new Label("Choix disponibles"), choices);

    /**
     * Crée une nouvelle Vue avec un titre et un presenter donné.
     *
     * @param viewName Titre de la vue.
     */
    public ModifyPageView(ViewName viewName, final ModifyPagePresenter presenter) {
        super(viewName);

        getChildren().addAll(header, optionPnl, choicesPnl, choiceForm);

        this.presenter = presenter;
        presenter.setView(this);
    }

    private void notifyOnChoiceCreated() {
        presenter.onChoiceCreated(choiceContent.getText(), choiceTarget.getSelectionModel().getSelectedItem().getItemContent());
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
    public void setChoices(Iterable<ListChoiceItem> choices) {
        this.choices.getChildren().clear();
        for (final var c : choices) {
            this.choices.getChildren().add(new ListItemPageView(c.getNumPage(), c.getContent(), presenter));
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
