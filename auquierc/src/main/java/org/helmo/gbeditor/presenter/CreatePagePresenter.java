package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Page;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.repositories.DataRepository;

import java.util.List;

/**
 * Gérer ce qui va être affiché à l'écran utilisateur et comment le programme va réagir aux évènements lancés par sa vue.
 */
public class CreatePagePresenter extends Presenter {

    private CreatePageInterface view;

    private final Session session;
    private final DataRepository repo;
    private Page currentPage;
    private Book currentBook;

    private static final String BEGIN_OPTION = "Au début du livre";
    private static final String END_OPTION = "A la fin du livre";
    private static final String AFTER_PAGE_OPTION = "Après une page";
    private static final List<String> ADD_OPTION = List.of(BEGIN_OPTION, END_OPTION, AFTER_PAGE_OPTION);

    /**
     * Crée un nouveau CreatePagePresenter avec une session et un DataRepository donné.
     *
     * @param session   Session actuelle sur laquelle l'application est lancée.
     * @param repo      Repository avec lequel l'application travaille.
     */
    public CreatePagePresenter(final Session session, final DataRepository repo) {
        this.session = session;
        this.repo = repo;
    }

    /**
     * Ajoute une page avec un contenu donnée et à un endroit donné.
     *
     * @param newPageContent        Contenu de la nouvelle page.
     * @param otherPageContent      Page après laquelle la nouvelle page va s'insérer (si nécessaire)
     * @param addOption             Endroit dans le livre où la nouvelle page va s'insérer.
     */
    public void onNotifyAddPage(final String newPageContent, final String otherPageContent, final String addOption) {
        if(newPageContent != null && !newPageContent.isEmpty()) {
            addPageToBook(newPageContent, otherPageContent, addOption);
            view.setMessage("La page a bien été créée.", TypeMessage.MESSAGE);
        } else {
            view.setMessage("Le contenu de la page ne peut pas être vide.", TypeMessage.ERROR);
        }
    }

    private void addPageToBook(final String newPageContent, final String otherPageContent, final String addOption) {
        currentPage.setContent(newPageContent);
        if(addOption.equals(BEGIN_OPTION)) {
            currentBook.addBegin(currentPage);
        } else if(addOption.equals(END_OPTION)) {
            currentBook.addEnd(currentPage);
        } else {
            var page = currentBook.getPageFor(otherPageContent);
            currentBook.addAfter(currentPage, page);
        }
        repo.save(currentBook);
        currentPage = new Page("");
    }

    /**
     * Régit à la selection d'une option d'ajout de page.
     *
     * @param option    Option déterminant où la page va s'insérer dans le livre.
     */
    public void onNotifyOptionSelected(final String option) {
        if(AFTER_PAGE_OPTION.equals(option)) {
            view.showBookPages(true);
        }
    }

    @Override
    public void onEnter(String fromView) {
        view.clearBookPages();
        currentPage = new Page("");
        currentBook = repo.searchBookFor(session.getCurrentIsbn());
        view.setAuthorName(session.getAuthor());
        view.setAddOptions(ADD_OPTION, BEGIN_OPTION);
        view.showBookPages(false);
        setBookPages();
    }

    private void setBookPages() {
        var book = repo.searchBookFor(session.getCurrentIsbn());
        for(final var p : book) {
            view.addBookPages(book.getNForPage(p), p.getContent());
        }
    }

    @Override
    public void onHomePressed() {
        view.showPopUp(ViewName.MANAGE_PAGE_VIEW.getName());
    }

    /**
     * Définit la vue avec laquelle le presenter va interagir.
     *
     * @param view  Vue avec laquelle interagir.
     */
    public void setView(final CreatePageInterface view) {
        this.view = view;
    }
}
