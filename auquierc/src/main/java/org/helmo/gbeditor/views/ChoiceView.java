package org.helmo.gbeditor.views;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ChoiceView extends Pane {

    private final ListItemPageView pageView;

    private final Label choiceLbl = new Label();

    public ChoiceView(final String choice, final int numPage, final String target) {
        choiceLbl.setText(choice);
        getChildren().addAll(choiceLbl, pageView = new ListItemPageView(numPage, target));
    }

    public String getContent() {
        return choiceLbl.getText();
    }

    public String getChoice() {
        return pageView.getItemContent();
    }

}
