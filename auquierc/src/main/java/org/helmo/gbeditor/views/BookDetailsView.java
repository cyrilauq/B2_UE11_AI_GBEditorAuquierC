package org.helmo.gbeditor.views;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.helmo.gbeditor.modeles.ExtendedBookDescription;
import org.helmo.gbeditor.presenter.BookDetailsHandler;
import org.helmo.gbeditor.views.style.Theme;

/**
 * Cette classe s'occupe de l'affichage des détails d'un livre et certaines actions à l'utilisateur(modifier, publier et gérer les pages).
 */
public class BookDetailsView extends VBox {

    private BookDetailsHandler handler;

    private final Text sldBookTitle = new Text(); {
        sldBookTitle.setText("Pas de livre sélectionné");
        sldBookTitle.setWrappingWidth(Theme.WINDOW_WIDTH * .32);
    }
    private final Label sldBookAuthor = new Label(); {
        sldBookAuthor.setWrapText(true);
    }
    private final Label sldBookISBN = new Label();
    private final Text selectedBookResume = new Text(); {
        selectedBookResume.setWrappingWidth(Theme.WINDOW_WIDTH * .32);
    }
    private final Button updateBtn = new Button("Modifier"); {
        updateBtn.setDisable(true);
//        updateBtn.setOnAction(a -> notifyOnModifyBook());
    }

    private final Button deleteBtn = new Button("Supprimer"); {
        deleteBtn.setDisable(true);
//        deleteBtn.setOnAction(a -> notifyOnDeleteButton());
    }

    private final Button publishBtn = new Button("Publier"); {
        publishBtn.setDisable(true);
    }

    private final ImageView sldBookImg = new ImageView(); {
        sldBookImg.setFitWidth(60);
        sldBookImg.setFitHeight(80);
        sldBookImg.setPreserveRatio(false);
        sldBookImg.setImage(new Image(Theme.DEFAULT_BOOK_COVER));
    }

    private final Button managePagesBtn = new Button("Gérer les pages"); {
        managePagesBtn.setDisable(true);
    }

    private final VBox sldBook = new VBox(); {
        var selectedBookHeader = new HBox();
        var selectedBookInfo = new VBox();
        var actionBtn = new HBox();
        actionBtn.getChildren().addAll(updateBtn, managePagesBtn, publishBtn);
        sldBook.getChildren().addAll(sldBookTitle, sldBookAuthor, sldBookISBN);
        selectedBookHeader.getChildren().addAll(sldBookImg, selectedBookInfo);
        sldBook.getChildren().addAll(selectedBookHeader, selectedBookResume, actionBtn);
        sldBook.setPrefWidth(Theme.WINDOW_WIDTH * .35);
        getChildren().add(sldBook);
    }

    /**
     * Définit l'handler de la BookDetailsView.
     *
     * @param handler   Handler avec lequel la vue interagira.
     */
    public void setHandler(BookDetailsHandler handler) {
        this.handler = handler;
        updateBtn.setOnAction(a -> handler.onModifyBook(sldBookISBN.getText()));
        publishBtn.setOnAction(a -> handler.onPublishBook(sldBookISBN.getText()));
        managePagesBtn.setOnAction(a -> handler.onManagePages(sldBookISBN.getText()));
    }

    /**
     * Définit les détails du livre à afficher.
     *
     * @param book  Détails du livre.
     */
    public void setBookDetails(final ExtendedBookDescription book) {
        setDetails(book);
        enableBtns(book.canBePublished());
    }

    private void setDetails(final ExtendedBookDescription book) {
        selectedBookResume.setText(book.getResume());
        sldBookTitle.setText(book.getTitle());
        sldBookAuthor.setText(book.getAuthor());
        sldBookISBN.setText(book.getIsbn());
        if(book.hasImg()) {
            sldBookImg.setImage(new Image(book.getImgPath()));
        }
    }

    /**
     * Rend l'interaction possible avec les bouttons autorisé de la vue.
     *
     * @param canBeModified Détermine si le livre peut être modifié et si les boutons seront actifs ou non.
     *                      Si canBeModified est à false, les boutons seront désactivés.
     *                      Si canBeModified est à true, alors les boutons seront activés.
     */
    private void enableBtns(final boolean canBeModified) {
        updateBtn.setDisable(canBeModified);
        publishBtn.setDisable(canBeModified);
        managePagesBtn.setDisable(canBeModified);
    }

}
