package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Page;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.repositories.DataRepository;

import java.util.List;

public class CreatePagePresenter extends Presenter {

    private CreatePageInterface view;

    private final Session session;
    private final DataRepository repo;
    private Page currentPage;
    private Book currentBook;

    private final String BEGIN_OPTION = "Au début du livre";
    private final String END_OPTION = "A la fin du livre";
    private final String AFTER_PAGE_OPTION = "Après une page";
    private final List<String> ADD_OPTION = List.of(BEGIN_OPTION, END_OPTION, AFTER_PAGE_OPTION);

    public CreatePagePresenter(final Session session, final DataRepository repo) {
        this.session = session;
        this.repo = repo;
        currentPage = new Page("");
    }

    public void onNotifyAddPage(final String newPageContent, final String otherPageContent, final String addOption) {
        if(newPageContent != null && !newPageContent.isEmpty()) {
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
        } else {
            view.setMessage("Le contenu de la page ne peut pas être vide.", TypeMessage.ERROR);
        }
    }

    public void onNotifyOptionSelected(final String option) {
        if(AFTER_PAGE_OPTION.equals(option)) {
            view.showBookPages(true);
        }
    }

    @Override
    public void onEnter(String fromView) {
        currentBook = repo.searchBookFor(session.getCurrentIsbn());
        view.setAuthorName(session.getAuthor());
        view.hideChoiceForm();
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
        view.goTo("HomeView");
    }

    public void setView(final CreatePageInterface view) {
        this.view = view;
    }

    public void onNotifyNewChoice() {
        view.showChoiceForm();
    }

    public void onNotifyNewChoice(final String choice, final String contentCiblePage) {
        if(choice == null || choice.isEmpty()) {
            view.setMessage("L'intitulé d'un choix ne peut pas être vide.");
        } else if(contentCiblePage == null || contentCiblePage.isEmpty()) {
            view.setMessage("Le choix doit obligatoirement pointé sur une page.");
        } else {
            currentPage.addChoice(choice.substring(choice.indexOf(":") + 2),
                    repo.searchBookFor(session.getCurrentIsbn()).getPageFor(contentCiblePage));
        }
    }

    public void onShowChoices() {
        view.showChoiceForm();
        var book = repo.searchBookFor(session.getCurrentIsbn());
        for(final var p : book) {
            view.addChoiceTarget("Page " + book.getNForPage(p) + ": " + p.getContent());
        }
    }
}
