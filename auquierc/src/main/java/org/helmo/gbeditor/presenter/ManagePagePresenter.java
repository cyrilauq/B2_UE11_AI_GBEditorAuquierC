package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.repositories.DataRepository;

/**
 * Gérer ce qui va être affiché à l'écran utilisateur et comment le programme va réagir aux évènements lancés par sa vue.
 */
public class ManagePagePresenter extends Presenter implements PageViewHandler {

    private ManagePageInterface view;
    private final Session session;
    private Book currentBook;
    private final DataRepository repo;

    /**
     * Crée un nouveau ManagePagePresenter à partir d'une Session et d'un DataReoisutory donné.
     *
     * @param session   Session courante.
     * @param repo      Repository courant.
     */
    public ManagePagePresenter(final Session session, final DataRepository repo) {
        this.session = session;
        this.repo = repo;
    }

    /**
     * Définit la vue avec laquelle le presenter va interagir.
     *
     * @param view  Vue avec laquelle interagir.
     */
    public void setView(final ManagePageInterface view) {
        this.view = view;
    }

    /**
     * Définit l'action à réaliser lorsque l'utilisateur souhaite créer une nouvelle page.
     */
    public void onNewPagePressed() {
        view.showPopUp(ViewName.CREATE_PAGE_VIEW.getName());
    }

    @Override
    public void onEnter(final String fromView) {
        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        view.clearPages();
        currentBook = session.getCurrentBook();
        currentBook.forEach(p -> view.addPage(currentBook.getNForPage(p), p.getContent(), currentBook.getPageBranchment(p)));
    }

    @Override
    public void onHomePressed() {
        view.showPopUp(ViewName.MODIFY_PAGE_VIEW.getName());
    }

    @Override
    public void onEdit(final String content) {
        session.setCurrentPageContent(content);
        view.showPopUp(ViewName.MODIFY_PAGE_VIEW.getName());
    }

    @Override
    public void onConfirmedDelete(String content) {
        if(currentBook.removePage(currentBook.getPageFor(content))) {
            repo.save(currentBook);
            view.refreshAll(ViewName.MANAGE_PAGE_VIEW);
        }
    }
}
