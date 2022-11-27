package org.helmo.gbeditor.views;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.helmo.gbeditor.presenter.ChoiceViewEventHandler;

public class ChoiceView extends HBox {

    private final Label choiceLbl = new Label();

    private final Button deleteBtn = new Button("Supprimer");

    public ChoiceView(final String choice, final int numPage, final String target, final ChoiceViewEventHandler handler) {
        choiceLbl.setText(choice);
        deleteBtn.setOnAction(a -> handler.onConfirmedDelete(choice));
        getChildren().addAll(new VBox(choiceLbl, new Label("Aller en page " + numPage + ": " + target)), deleteBtn);
    }

    public String getChoice() {
        return choiceLbl.getText();
    }

}
