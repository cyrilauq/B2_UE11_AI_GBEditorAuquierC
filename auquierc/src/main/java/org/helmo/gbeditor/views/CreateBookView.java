package org.helmo.gbeditor.views;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.helmo.gbeditor.presenter.TypeMessage;
import org.helmo.gbeditor.presenter.CreateBookInterface;
import org.helmo.gbeditor.presenter.CreateBookPresenter;
import org.helmo.gbeditor.presenter.ViewName;
import org.helmo.gbeditor.views.style.Theme;

import java.io.File;

public class CreateBookView extends View implements CreateBookInterface {

    private final CreateBookPresenter presenter;

    private final HeaderView header = new HeaderView("Création de livre-jeu", "Home", Theme.SND_WINDOW_WIDTH); {
        header.setOnBtnAction(a -> notifyOnHomePressed());
    }

    private final Label titleLbl = new Label("Titre: ");
    private final TextArea titleFld = new TextArea(); {
        titleFld.setPrefColumnCount(150);
        titleFld.setPrefRowCount(10);
        titleFld.setWrapText(true);
        titleFld.addEventHandler(KeyEvent.KEY_TYPED, new KeyTypesHandler(titleFld, null, 150));
    }

    private final Label isbnLbl = new Label("ISBN: ");
    private final TextField isbnFld = new TextField();
    private final Label resumeLbl = new Label("Resumé: ");

    private final Label nbCharLbl = new Label(); {
        nbCharLbl.setAlignment(Pos.BOTTOM_RIGHT);
    }

    private final TextArea resumeFld = new TextArea(); {
        resumeFld.setPrefColumnCount(150);
        resumeFld.setPrefRowCount(20);
        resumeFld.setWrapText(true);
        resumeFld.addEventHandler(KeyEvent.KEY_TYPED, new KeyTypesHandler(resumeFld, nbCharLbl, 500));
    }

    private final Button createBtn = new Button("Créer le livre"); {
        createBtn.setOnAction(a -> onCreateNewBook());
    }

    private final Label messageLbl = new Label(); {
        messageLbl.getStyleClass().add("error");
    }

    private final FileChooser fileChooser = new FileChooser();

    private File choosedFile;

    private final ImageView choosedFileView = new ImageView(); {
        choosedFileView.setFitHeight(100);
        choosedFileView.setFitWidth(50);
        choosedFileView.setPreserveRatio(false);
        choosedFileView.setImage(new Image(Theme.DEFAULT_BOOK_COVER));
    }

    private final Button fileChooserBtn = new Button("Choisir un fichier: "); {
        fileChooserBtn.setOnAction(a -> {
            choosedFile = fileChooser.showOpenDialog(getScene().getWindow());
            choosedFileView.setImage(new Image(choosedFile == null ? Theme.DEFAULT_BOOK_COVER : choosedFile.getPath()));
        });
    }

    private final Pane filePnl = new Pane(); {
        filePnl.getChildren().addAll(choosedFileView, fileChooserBtn);
    }

    private final Label errorMsgLbl = new Label();

    private final GridPane form = new GridPane(); {
        form.getStyleClass().add("content-root");
        form.add(titleLbl, 0, 1);
        form.add(titleFld, 2, 1);
        form.add(isbnLbl, 0, 2);
        form.add(isbnFld, 2, 2);
        form.add(resumeLbl, 0, 3);
        form.add(resumeFld, 2, 3);
        form.add(nbCharLbl, 2, 3);
        form.add(new Label("Choisir une couverture:"), 0, 4);
        form.add(filePnl, 2, 4);
        form.add(createBtn, 1, 5, 2, 1);
        form.add(errorMsgLbl, 1, 6);
        form.setVgap(5);
        form.setHgap(5);
        form.setPadding(new Insets(10, 10, 10, 10));
        form.setMaxHeight(350);
        form.setMaxWidth(350);
        form.getColumnConstraints().addAll(
                new ColumnConstraints(350 * .7 / 3),
                new ColumnConstraints(10),
                new ColumnConstraints(350 * .8)
        );
    }

    /**
     * Crée une nouvelle CreateBookView avec un titre et un presenter donné.
     *
     * @param viewName         Titre de la vue
     * @param presenter     Presenter avec lequel la vue interagira.
     */
    public CreateBookView(final ViewName viewName, CreateBookPresenter presenter) {
        super(viewName);
        getChildren().addAll(header, form, messageLbl);
        setAlignment(Pos.TOP_CENTER);

        this.presenter = presenter;
        this.presenter.setView(this);
    }

    private void onCreateNewBook() {
        presenter.createNewBook(titleFld.getText(),
                isbnFld.getText(),
                resumeFld.getText(),
                choosedFile == null ? "" : choosedFile.getPath());
    }

    private void notifyOnHomePressed() { presenter.onHomePressed(); }

    @Override
    public void setMessage(String txt, TypeMessage type) {
        messageLbl.setText(txt);
    }

    @Override
    public void onEnter(String fromView) {
        presenter.onEnter(fromView);
    }

    @Override
    public void setAuthorName(String name) {
        header.setAuthorName(name);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        if(keyCode == KeyCode.ENTER.getCode()) {
            onCreateNewBook();
        }
    }

    @Override
    public void setResumeTxt(String txt) {
        resumeFld.setText(txt);
    }

    @Override
    public void resetInputs() {
        titleFld.setText("");
        resumeFld.setText("");
        isbnFld.setText("");
        choosedFileView.setImage(null);
        nbCharLbl.setText("0/500");
    }

    @Override
    public void onLeave(String fromView) {
        presenter.onLeave(fromView);
    }

    @Override
    public void setIsbn(String isbn) {
        isbnFld.setText(isbn);
    }
}
