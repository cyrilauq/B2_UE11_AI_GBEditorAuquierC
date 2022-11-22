package org.helmo.gbeditor.views;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.helmo.gbeditor.modeles.LittleBookDescription;
import org.helmo.gbeditor.presenter.BookDescriptionHandler;
import org.helmo.gbeditor.views.style.Theme;

/**
 * Cette classe s'occupe de la vue pour l'affichage de la description d'un livre.
 */
public class BookDescriptionView extends VBox {
    private final Label title = new Label(); {
        title.getStyleClass().add("book-title");
        title.setWrapText(true);
    }

    private final Label author = new Label(); {
        author.getStyleClass().add("book-author");
        author.setWrapText(true);
    }

    private final Label isbn = new Label(); {
        isbn.getStyleClass().add("book-isbn");
        isbn.setWrapText(true);
    }

    private final ImageView img = new ImageView(); {
        img.setFitWidth(60);
        img.setFitHeight(80);
        img.setPreserveRatio(false);
        img.setImage(getImgFor(Theme.DEFAULT_BOOK_COVER));
    }

    private final Label publishState = new Label();

    /**
     * Permet de créer une vue de description de livre.
     *
     * @param book      Description du livre à afficher.
     * @param handler
     */
    public BookDescriptionView(final LittleBookDescription book, final BookDescriptionHandler handler) {
        setPrefWidth(100);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());
        if (book.hasImg()) {
            img.setImage(getImgFor(book.getImgPath()));
        }

        getChildren().addAll(title, img, isbn, author);
        setOnMouseClicked(e -> handler.displayDetailsFor(book.getIsbn()));
    }

    private Image getImgFor(final String path) {
        return new ImageView(path).getImage();
    }
}
