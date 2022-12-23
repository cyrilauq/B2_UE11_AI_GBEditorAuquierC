package org.helmo.gbeditor.views;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.helmo.gbeditor.presenter.viewmodels.PageBookDescription;
import org.helmo.gbeditor.presenter.PageViewHandler;

/**
 * Cette classe définit un composant qui permettra d'afficher les pages d'un livre lorsque la discription de ce dernier sera affichée.
 */
public class PageBookDescriptionView extends HBox {

    private final Button deleteBtn = new Button("Supprimer");
    private final Button modifyBtn = new Button("Modifier page");

    private final Alert confirmDltBox = new Alert(Alert.AlertType.CONFIRMATION); {
        confirmDltBox.setTitle("Confirmation");
        confirmDltBox.setHeaderText("Suppression de la page");
        confirmDltBox.setContentText("Voulez-vous vraiment supprimer cette page ?");
        confirmDltBox.hide();
    }

    /**
     * Créer une nouvelle PageBookDescriptionView
     *
     * @param description   Modèle contenant les informations liées à la page.
     * @param handler       Handler pour les évènements liés à la page.
     */
    public PageBookDescriptionView(final PageBookDescription description, final PageViewHandler handler) {
        if(handler == null) {
            getChildren().addAll(new Label(description.getContent().substring(0, Math.min(description.getContent().length(), 20)) + " ..."));
        } else {
            deleteBtn.setOnAction(a -> {
                if(confirmDltBox.showAndWait().get() == ButtonType.OK) {
                    handler.onConfirmedDelete(description.getContent());
                    confirmDltBox.hide();
                }
            });
            modifyBtn.setOnAction(a -> handler.onEdit(description.getContent()));
            getChildren().addAll(new Label(description.getContent().substring(0, Math.min(description.getContent().length(), 20)) + " ..."), modifyBtn, deleteBtn);
        }
    }

}
