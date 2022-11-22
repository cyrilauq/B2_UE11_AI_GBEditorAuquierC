package org.helmo.gbeditor.views;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.helmo.gbeditor.presenter.PageViewHandler;

public class ListItemPageView extends VBox {
    private final Button editBtn = new Button("Editer");

    private final Button confirmDeleteBtn = new Button("Oui");
    private final Button cancelDeleteBtn = new Button("Non");

    private final Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION); {
        confirmDelete.setTitle("Confirmation");
        confirmDelete.setHeaderText("Suppression de la page");
        confirmDelete.setContentText("Voulez-vous vraiment supprimer cette page ?");
        confirmDelete.hide();
    }

    private final Button deleteBtn = new Button("Supprimer");

    private final Label contentLbl = new Label();

    public ListItemPageView(final int numPage, final String content, final PageViewHandler handler) {
        editBtn.setOnAction(a -> handler.onEdit(content));

        contentLbl.setText(content);
        editBtn.setOnAction(e -> handler.onEdit(content));
        deleteBtn.setOnAction(e -> {
            var btns = confirmDelete.showAndWait();
            if(btns.get() == ButtonType.OK) {
                handler.onConfirmedDelete(content);
                confirmDelete.hide();
            }
        });
        getChildren().addAll(new Label("" + numPage), contentLbl, editBtn, deleteBtn);
    }

    public ListItemPageView(final int numPage, final String content) {
        contentLbl.setText(content);
        getChildren().addAll(new Label("" + numPage), contentLbl);
    }

    public String getItemContent() {
        return contentLbl.getText();
    }
}
