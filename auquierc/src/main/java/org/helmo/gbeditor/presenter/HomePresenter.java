package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.exception.UnableToOpenResourceException;
import org.helmo.gbeditor.modeles.ExtendedBookDescription;
import org.helmo.gbeditor.modeles.LittleBookDescription;
import org.helmo.gbeditor.repositories.DataRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Gérer ce qui va être affiché à l'écran utilisateur et comment le programme va réagir aux évènements lancés par sa vue.
 */
public class HomePresenter extends Presenter implements BookDescriptionHandler, BookDetailsHandler {
    private final static int MAX_BOOKS_PAGE = 8;

    private HomeInterface view;
    private final DataRepository repo;
    private final Session session;
    private Book currentBook;

    private int currentPage;

    private final List<Book> books = new ArrayList<>();

    /**
     * Crée un nouvel objet HomePresenter.
     *
     * @param session   Session courante de l'utilisateur.
     * @param repo
     */
    public HomePresenter(final Session session, final DataRepository repo) {
        this.repo = repo;
        this.session = session;
    }

    @Override
    public void onEnter(String fromView) {
        currentPage = 0;
        try {
            refresh();
        } catch (UnableToOpenResourceException e) {
            view.setMessage(e.getMessage(), TypeMessage.ERROR);
        }
    }

    public void setView(final HomeInterface view) {
        this.view = view;
    }

    private void refresh() {
        displayBooks();
        view.setCurrentPage(currentPage + 1);
        view.setAuthorName(session.getAuthor());
    }

    private void displayBooks() {
        view.clearBooks();
        books.clear();
        if(books.size() == 0) {
            books.addAll(repo.getBooks());
        }
        if(books.size() > 0) {
            for(int i = currentPage * MAX_BOOKS_PAGE; i < ((currentPage + 1) * MAX_BOOKS_PAGE) && i < books.size(); i++) {
                var b = books.get(i);
                var title = b.getTitle().length() > 15 ? b.getTitle().substring(0, 15) + "..." : b.getTitle();
                view.addBook(new LittleBookDescription(title,
                        b.getImgPath(), b.getIsbn(), b.getAuthor(),
                        b.getPublishDate()));
            }
            displayDetailsFor(books.get(currentPage * MAX_BOOKS_PAGE).getIsbn());
        } else {
            view.setMessage("Vous n'avez pas encore créé de livre.", TypeMessage.MESSAGE);
        }
    }

    /**
     * Affiche les détails d'un livre dont l'ISBN est connu.
     *
     * @param isbn  ISBN du livre à afficher.
     */
    public void displayDetailsFor(final String isbn) {
        var found = repo.searchBookFor(isbn);
        if(found != null) {
            session.setCurrentBook(currentBook = found);
            session.setCurrentIsbn(isbn);
            view.setDetails(new ExtendedBookDescription(
                    new LittleBookDescription(
                            found.getTitle(),
                            found.getImgPath(),
                            found.getIsbn(),
                            found.getAuthor(),
                            found.getPublishDate()),
                    found.getResume()
            ));
            found.forEach(p -> {
                view.addAvailablePages(found.getNPageFor(p), p.getContent());
            });
        }
    }

    /**
     * Affiche les livres de la page suivante.
     */
    public void onNextPagePressed() {
        if((currentPage + 1) * MAX_BOOKS_PAGE < books.size()) {
            currentPage++;
            refresh();
        }
    }

    /**
     * Affiche les livres de la page précendente.
     */
    public void onPreviousPagePressed() {
        if((currentPage - 1) > -1) {
            currentPage--;
            refresh();
        }
    }

    /**
     * Renvoie l'utilisateur vers la vue de création de livre.
     */
    public void onCreate() {
        view.showPopUp("CreateBookView");
    }

    /**
     * Réagit au clic du bouton 'Supprimer' d'une page en affichant la pop-up de confirmation.
     *
     * @param pageContent  Page à supprimer.
     */
    public void onDeletePageRequested(final String pageContent) {
        if(pageContent == null || pageContent.isEmpty()) {
            return;
        }
        session.setCurrentPageContent(pageContent);
        // TODO : Afficher la pop-up de confirmation.
    }

    /**
     * Supprime la page choisie par l'utilisateur après qu'il est supprimé sa suppression.
     *
     * @param pageContent  Contenu de la page à supprimer.
     */
    public void onDeletePageConfirmed(final String pageContent) {
        if(pageContent == null || pageContent.isEmpty()) {
            return;
        }
        // TODO : Supprimer la page.
        refresh();
    }

    public void onModifyPage(final String pageContent) {
        if(pageContent == null || pageContent.isEmpty()) {
            return;
        }
        session.setCurrentPageContent(pageContent);
        // TODO : Créer la vue ModifyPageVue (pour pouvoir modifier une page)
//        view.showPopUp("ModifyPageView");
    }

    @Override
    public void onPublishBook(String isbn) {
        if(isbn == null || isbn.isEmpty() || currentBook == null) { return; }
        // TODO : Demander confirmation pour la publication du livre. Expliquer à l'utilisateur qu'il ne pourra plus le modifier.
        currentBook.publish();
        repo.save(currentBook);
    }

    /**
     * Modifie le livre possédant l'ISBN voulu.
     *
     * @param isbn  ISBN du livre à modifier.
     */
    @Override
    public void onModifyBook(final String isbn) {
        if(isbn == null || isbn.isEmpty()) { return; }
        session.setCurrentIsbn(isbn);
        view.goTo("ModifyBookView");
    }

    @Override
    public void onManagePages(String isbn) {
        if(isbn == null || isbn.isEmpty()) { return; }
        // TODO : Créer la vue ManagePagesView, permet d'ajouter une page, supprimer un page ou modifier une page.
        view.goTo(ViewName.MANAGA_PAGE_VIEW.getName());
    }

    /**
     * Supprime le livre possédant l'ISBN voulu.
     *
     * @param isbn  ISBN du livre à supprimer.
     */
    public void onDeleteBook(final String isbn) {
        if(isbn == null || isbn.isEmpty()) { return; }

    }

    /**
     * Affiche le formulaire de création de page.
     */
    public void onNotifyNewPage() {
        view.showPopUp("CreatePageView");
    }

    @Override
    public void onRefresh() {
        repo.loadBooks();
        refresh();
    }
}
