package org.helmo.gbeditor.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.helmo.gbeditor.views.style.Theme;

/**
 * Cette classe permet de créer un header uniformisé pour chacune des vues.
 */
public class HeaderView extends GridPane {

    private final Label titleLbl = new Label(); {
        titleLbl.getStyleClass().addAll("title");
    }
    private double width = Theme.WINDOW_WIDTH * 1.0;

    private final Button homeBtn = new Button("Home"); {
        homeBtn.setFocusTraversable(false);
    }

    private final Label authorLbl = new Label(); {
        authorLbl.getStyleClass().add("author-lbl");
    }

    /**
     * Crée un nouvel objet HeaderView
     *
     * @param title     Titre du header
     * @param btnTxt    Texte à afficher dans le button du header.
     */
    public HeaderView(final String title, final String btnTxt) {
        titleLbl.setText(title);
        homeBtn.setText(btnTxt);
        setPrefWidth(width);
        setPadding(new Insets(10));
        add(titleLbl, 0, 0, 3, 1);
        add(authorLbl, 2, 1, 1, 1);
        add(homeBtn, 0, 1, 1, 1);
        getColumnConstraints().addAll(
                new ColumnConstraints(width / 3),
                new ColumnConstraints(width / 3),
                new ColumnConstraints(width / 3)
        );
        titleLbl.setPrefWidth(width);
        setAlignment(Pos.TOP_CENTER);
    }

    /**
     * Crée un nouvel objet HeaderView
     *
     * @param title     Titre du header
     * @param btnTxt    Texte à afficher dans le button du header.
     * @param width     Largeur du header.
     */
    public HeaderView(final String title, final String btnTxt, final double width) {
        this(title, btnTxt);
        this.width = width;
    }

    /**
     * Affecte une action au button.
     *
     * @param action    Action à affecter au boutton.
     */
    public void setOnBtnAction(final EventHandler<ActionEvent> action) {
        homeBtn.setOnAction(action);
    }

    /**
     * Affiche le nom d'un auteur donné à l'écran.
     *
     * @param author    Nom de l'auteur à afficher.
     */
    public void setAuthorName(final String author) {
        authorLbl.setText(author);
    }

}
