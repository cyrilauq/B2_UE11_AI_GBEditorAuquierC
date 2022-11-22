package org.helmo.gbeditor.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class PopupPaneFactory {

    public static FlowPane newPane(final String lblTxt) {

        FlowPane deletePagePopupContent = new FlowPane(); {
            deletePagePopupContent.getChildren().addAll(
                    new Label(lblTxt)
            );
            deletePagePopupContent.setAlignment(Pos.CENTER);
            deletePagePopupContent.setPrefWidth(120);
        }
        return deletePagePopupContent;
    }

}
