package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.BookFieldName;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.exception.DataManipulationException;
import org.helmo.gbeditor.infrastructures.exception.UnableToOpenResourceException;
import org.helmo.gbeditor.modeles.ExtendedBookDescription;
import org.helmo.gbeditor.modeles.LittleBookDescription;
import org.helmo.gbeditor.repositories.DataRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Gérer ce qui va être affiché à l'écran utilisateur et comment le programme va réagir aux évènements lancés par sa vue.
 *
 *  TODO : Insérer une page récupéré l'ID généré de la page et l'ajouter dans le DTO pour pouvoir faire la modif après.
 */
public class HomePresenter extends Presenter implements BookDescriptionHandler, BookDetailsHandler {
    private final static int MAX_BOOKS_PAGE = 8;
    // TODO : Load livre dans la methode getBookFor(final String isbn);
    private HomeInterface view;
    private final DataRepository repo;
    private final Session session;

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

    /**
     * Définit la vue avec laquelle le presenter va interagir.
     *
     * @param view  Vue avec laquelle interagir.
     */
    public void setView(final HomeInterface view) {
        this.view = view;
    }

    private void refresh() {
        displayBooks();
        view.setCurrentPage(currentPage + 1);
        view.setAuthorName(session.getAuthor());
    }

    private void displayBooks() {
        try {
            view.clearBooks();
            books.clear();
            books.addAll(repo.getBooks());
            if(books.size() > 0) {
                for(int i = currentPage * MAX_BOOKS_PAGE; i < ((currentPage + 1) * MAX_BOOKS_PAGE) && i < books.size(); i++) {
                    var b = books.get(i);
                    var title = b.get(BookFieldName.TITLE).length() > 15 ? b.get(BookFieldName.TITLE).substring(0, 15) + "..." : b.get(BookFieldName.TITLE);
                    view.addBook(new LittleBookDescription(title,
                            b.getImgPath(), b.get(BookFieldName.ISBN), b.get(BookFieldName.AUTHOR),
                            b.get(BookFieldName.PUBLISH_DATE)));
                }
                displayDetailsFor(books.get(currentPage * MAX_BOOKS_PAGE).get(BookFieldName.ISBN));
            } else {
                view.setMessage("Vous n'avez pas encore créé de livre.", TypeMessage.MESSAGE);
            }
        } catch (DataManipulationException ignored) {
            view.setMessage("Les livres n'ont pas pu être chargé.", TypeMessage.ERROR);
        }
    }

    /**
     * Affiche les détails d'un livre dont l'ISBN est connu.
     *
     * @param isbn  ISBN du livre à afficher.
     */
    @Override
    public void displayDetailsFor(final String isbn) {
        try {
            var found = repo.searchBookFor(isbn);
            if(found != null) {
                session.setCurrentBook(found);
                session.setCurrentBook(found);
                session.setCurrentIsbn(isbn);
                view.setDetails(getExtendedBookDescriptionFor(found));
                found.forEach(p -> {
                    view.addAvailablePages(found.getNPageFor(p), p.getContent());
                });
            }
        } catch (DataManipulationException e) {
            view.setMessage("Le livre n'a pas pu être récupéré.", TypeMessage.ERROR);
        }
    }

    private static ExtendedBookDescription getExtendedBookDescriptionFor(Book found) {
        return new ExtendedBookDescription(
                getLittleBookDescriptionFor(found),
                found.get(BookFieldName.SUMMARY)
        );
    }

    private static LittleBookDescription getLittleBookDescriptionFor(Book found) {
        return new LittleBookDescription(
                found.get(BookFieldName.TITLE),
                found.getImgPath(),
                found.get(BookFieldName.ISBN),
                found.get(BookFieldName.AUTHOR),
                found.get(BookFieldName.PUBLISH_DATE));
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
        view.showPopUp(ViewName.CREATE_BOOK_VIEW.getName());
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

    /**
     * Réagit à l'évènement de modification de page ayant le contenu donné et rédirige l'utilsateur vers las vue de modification de page..
     * Le page sera modifiable si et seulement si le choix est non null et non vide.
     *
     * @param pageContent   Contenu de la nouvelle page.
     */
    public void onModifyPage(final String pageContent) {
        if(pageContent == null || pageContent.isEmpty()) {
            return;
        }
        session.setCurrentPageContent(pageContent);
        view.goTo(ViewName.MODIFY_PAGE_VIEW.getName());
    }

    @Override
    public void onPublishBook(String isbn) {
        final var currentBook = session.getCurrentBook();
        if(isbn == null || isbn.isEmpty() || currentBook == null) { return; }
        // TODO : Demander confirmation pour la publication du livre. Expliquer à l'utilisateur qu'il ne pourra plus le modifier.
        try {
            currentBook.publish();
            repo.save(currentBook);
        } catch (DataManipulationException e) {
            view.setMessage("Le livre n'a pas pu être publié.", TypeMessage.ERROR);
        }
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
        view.goTo(ViewName.MODIFY_BOOK_VIEW.getName());
    }

    @Override
    public void onManagePages(String isbn) {
        if(isbn == null || isbn.isEmpty()) { return; }
        // TODO : Créer la vue ManagePagesView, permet d'ajouter une page, supprimer un page ou modifier une page.
        view.goTo(ViewName.MANAGE_PAGE_VIEW.getName());
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
        view.showPopUp(ViewName.CREATE_PAGE_VIEW.getName());
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
