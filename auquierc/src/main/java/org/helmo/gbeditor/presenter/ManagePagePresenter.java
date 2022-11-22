package org.helmo.gbeditor.presenter;

import javafx.scene.layout.FlowPane;
import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.repositories.DataRepository;

public class ManagePagePresenter extends Presenter implements PageViewHandler {

    private ManagePageInterface view;
    private final Session session;
    private Book currentBook;
    private final DataRepository repo;
    private final FlowPane pane = new FlowPane();

    public ManagePagePresenter(final Session session, final DataRepository repo) {
        this.session = session;
        this.repo = repo;
    }

    public void setView(final ManagePageInterface view) {
        this.view = view;
    }

    public void onNewPagePressed() {
        view.goTo(ViewName.CREATE_PAGE_VIEW.getName());
    }

    @Override
    public void onEnter(String fromView) {
        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        view.clearPages();
        currentBook = session.getCurrentBook();
        currentBook.forEach(p -> view.addPage(currentBook.getNForPage(p), p.getContent()));
    }

    @Override
    public void onHomePressed() {
        view.goTo(ViewName.HOME_VIEW.getName());
    }

    @Override
    public void onEdit(final String content) {
        session.setCurrentPageContent(content);
        view.goTo(ViewName.MODIFY_PAGE_VIEW.getName());
    }

    @Override
    public void onConfirmedDelete(String content) {
        System.err.println("On confirmed delete clicked for page " + content);
        currentBook.removePage(currentBook.getPageFor(content));
        if(!currentBook.containsPage(currentBook.getPageFor(content))) {
            System.err.println("The delete is seccussfull");
            repo.save(currentBook);
            refresh();
        }
    }
}
