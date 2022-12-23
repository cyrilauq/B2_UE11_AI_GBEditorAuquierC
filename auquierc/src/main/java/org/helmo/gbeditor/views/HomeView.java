package org.helmo.gbeditor.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import org.helmo.gbeditor.presenter.viewmodels.ExtendedBookDescription;
import org.helmo.gbeditor.presenter.viewmodels.LittleBookDescription;
import org.helmo.gbeditor.presenter.HomeInterface;
import org.helmo.gbeditor.presenter.HomePresenter;
import org.helmo.gbeditor.presenter.TypeMessage;
import org.helmo.gbeditor.presenter.ViewName;
import org.helmo.gbeditor.views.style.Theme;

/**
 * Cette classe s'occupe de l'affiche de la page d'accueil.
 */
public class HomeView extends View implements HomeInterface {

    private final HeaderView header = new HeaderView("Accueil", "Créer livre"); {
        header.setOnBtnAction(a -> notifyOnCreatePressed());
    }

    private final FlowPane booksHolder = new FlowPane(); {
        booksHolder.setPrefWidth(Theme.WINDOW_WIDTH * .6);
    }

    private final BookDetailsView sldBook = new BookDetailsView();

    private final HBox rootBody = new HBox(); {
        rootBody.getChildren().addAll(booksHolder, sldBook);
        rootBody.setAlignment(Pos.CENTER);
    }

    private final Button previousBtn = new Button("Prev");

    private final Button nextBtn = new Button("Next");

    private final Label currentPage = new Label("1");

    private final HBox navigationBar = new HBox(); {
        navigationBar.getChildren().addAll(previousBtn, currentPage, nextBtn);
    }

    private final HomePresenter presenter;

    private final Button yesBtnPopupPage = new Button("Oui");
    private final Button noBtnPopupPage = new Button("Non");

    private final FlowPane deletePagePopupContent = PopupPaneFactory.newPane("Êtes-vous sûr de vouloir supprimer la page?"); {
        deletePagePopupContent.getChildren().addAll(
                yesBtnPopupPage,
                noBtnPopupPage);
    }

    private final Popup deletePagePopup = new Popup(); {
        deletePagePopup.getContent().addAll(deletePagePopupContent);
        noBtnPopupPage.setOnAction(a -> deletePagePopup.hide());
    }

    private final Label messageLbl = new Label();

    /**
     * Créer une nouvelle HomeView avec un titre et un presenter donné.
     *
     * @param viewName  Titre de la vue.
     * @param presenter Presenter de la vue.
     */
    public HomeView(final ViewName viewName, final HomePresenter presenter) {
        super(viewName);

        getChildren().addAll(header, messageLbl, rootBody, navigationBar);

        this.presenter = presenter;
        this.presenter.setView(this);
        sldBook.setHandler(this.presenter);
        previousBtn.setOnAction(a -> presenter.onMovePage(-1));
        nextBtn.setOnAction(a -> presenter.onMovePage(1));
    }

    // TODO : Changer la tailles des 2 fenêtre pour qu'elles ne se supperposents plus et évite tout problèmes avec les boutons

    private void notifyOnPageEdit(final String pageContent) {
        presenter.onModifyPage(pageContent);
    }

    private void notifyOnDeletePage(final String pageContent) {
        presenter.onDeletePageRequested(pageContent);
    }

    private void notifyOnCreatePressed() {
        presenter.onCreate();
    }

    @Override
    public void addAvailablePages(int num, String content) {

    }

    @Override
    public void setMessage(String txt, TypeMessage message) {
        messageLbl.setText(txt);
    }

    @Override
    public void addBook(final LittleBookDescription bookDescription) {
        var book = new BookDescriptionView(bookDescription, presenter);
        booksHolder.getChildren().add(book);
    }

    @Override
    public void setAuthorName(String name) {
        header.setAuthorName(name);
    }

    @Override
    public void setDetails(ExtendedBookDescription bookDescription) {

        sldBook.setBookDetails(bookDescription);
        sldBook.addPages(bookDescription, bookDescription.canBePublished() ? presenter : null);
    }

    @Override
    public void clearBooks() {
        booksHolder.getChildren().clear();
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage.setText("" + currentPage);
    }

    @Override
    public void onEnter(String fromView) {
        presenter.onEnter(fromView);
    }

    @Override
    public void refresh() {
        presenter.onRefresh();
    }
}
