package org.helmo.gbeditor.views;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.helmo.gbeditor.presenter.viewmodels.ExtendedBookDescription;
import org.helmo.gbeditor.presenter.viewmodels.PageBookDescription;
import org.helmo.gbeditor.presenter.BookDetailsEventHandler;
import org.helmo.gbeditor.presenter.PageViewHandler;
import org.helmo.gbeditor.views.style.Theme;

/**
 * Cette classe s'occupe de l'affichage des détails d'un livre et certaines actions à l'utilisateur(modifier, publier et gérer les pages).
 */
public class BookDetailsView extends VBox {

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
    }

    private final Button deleteBtn = new Button("Supprimer"); {
        deleteBtn.setDisable(true);
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

    private final VBox pagePnl = new VBox();
    private final ScrollPane pageScrollPnl = new ScrollPane(); {
        pageScrollPnl.setPrefHeight(200);
        pageScrollPnl.setContent(pagePnl);
        pageScrollPnl.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        pageScrollPnl.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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
        getChildren().addAll(sldBook, pageScrollPnl);
    }

    private final GridPane pagesPnl = new GridPane(); {
        pagesPnl.add(new Label("N°"), 0, 0);
        pagesPnl.add(new Label("Contenu"), 1, 0);
        pagesPnl.add(new Label("Option"), 2, 0);
    }

    /**
     * Définit le handler de la BookDetailsView.
     *
     * @param handler   Handler avec lequel la vue interagira.
     */
    public void setHandler(BookDetailsEventHandler handler) {
        updateBtn.setOnAction(a -> handler.onModifyBook(sldBookISBN.getText()));
        publishBtn.setOnAction(a -> handler.onPublishBook(sldBookISBN.getText()));
        managePagesBtn.setOnAction(a -> handler.onManagePages(sldBookISBN.getText()));
    }

    public void addPages(Iterable<PageBookDescription> pages, PageViewHandler handler) {
        pagePnl.getChildren().clear();
        for(final var p : pages) {
            pagePnl.getChildren().add(new PageBookDescriptionView(p, handler));
        }
    }

    /**
     * Définit les détails du livre à afficher.
     *
     * @param book  Détails du livre.
     */
    public void setBookDetails(final ExtendedBookDescription book) {
        setDetails(book);
        enableBtns(!book.canBePublished());
    }

    private void setDetails(final ExtendedBookDescription book) {
        selectedBookResume.setText(book.getResume());
        sldBookTitle.setText(book.getTitle());
        sldBookAuthor.setText(book.getAuthor());
        sldBookISBN.setText(book.getIsbn());
        sldBookImg.setImage(new Image(book.hasImg() ? book.getImgPath() : Theme.DEFAULT_BOOK_COVER));
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
