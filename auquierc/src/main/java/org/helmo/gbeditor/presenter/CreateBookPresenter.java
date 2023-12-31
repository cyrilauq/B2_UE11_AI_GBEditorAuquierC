package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.*;
import org.helmo.gbeditor.domains.factory.BookFactory;
import org.helmo.gbeditor.domains.factory.ISBNFactory;
import org.helmo.gbeditor.repositories.exceptions.BookAlreadyExistsException;
import org.helmo.gbeditor.repositories.DataRepository;
import org.helmo.gbeditor.repositories.FileUtils;
import org.helmo.gbeditor.repositories.exceptions.DataManipulationException;

import java.nio.file.Paths;

/**
 * Cette classe s'occupe de la logique qui se cache derrière les actions de la vue CreateBookView.
 *
 * @author  Cyril Auquier
 */
public class CreateBookPresenter extends Presenter {
    private final Session session;

    private final DataRepository repo;
    private CreateBookInterface view;

    private static final int LANG_CODE = 2;

    /**
     * Crée un nouveau CreateBookPresenter à partir d'une Session donnée.
     *
     * @param session   Session courante.
     */
    public CreateBookPresenter(final Session session, final DataRepository repo) {
        this.repo = repo;
        this.session = session;
    }

    /**
     * Crée un nouveau livre à partir d'un titre, un code ISBN et un résumé donné.
     * Si un de ces champs est vide ou jugé non valide, la création du livre sera annulée.
     *
     * @param title     Titre du livre.
     * @param isbn      Code ISBN du livre.
     * @param resume    Résumé du livre.
     */
    public void createNewBook(final String title, final String isbn, final String resume, final String filePath) {
        try {
            var newBook = getBookFor(
                    new BookMetadata(title, isbn, resume, session.getAuthor()),
                    session.getMatricule().substring(1),
                    filePath);
            repo.add(newBook);
            view.resetInputs();
            view.setIsbn(
                    ISBNFactory.computeISBNFor(LANG_CODE,
                            session.getMatricule().substring(1),
                            (getLastBookNumber()) + 1).forUser());
            setMessage("Le livre a bien été créé.");
            view.refreshAll(ViewName.CREATE_BOOK_VIEW);
        } catch (BookAlreadyExistsException | Book.WrongFormattedBookException | ISBN.WrongFormattedISBNException e) {
            setMessage(e.getMessage());
        }
    }

    private Book getBookFor(final BookMetadata bookMetadata, String authorMatricul, final String filePath) {
        if(filePath == null || filePath.isBlank()) {
            return BookFactory.of(bookMetadata, authorMatricul);
        }
        return BookFactory.of(bookMetadata, authorMatricul,
                FileUtils.copyFile(filePath,
                        Paths.get(System.getProperty("user.home"), "ue36", "e200106").toAbsolutePath().toString(),
                        bookMetadata.get(BookFieldName.ISBN)
                )
        );
    }

    private void setMessage(String message) {
        view.setMessage(message, TypeMessage.MESSAGE);
    }

    /**
     * Définit les actions réalisées par le presenter lorsque la vue fera appel à lui.
     *
     * @param fromView  Nom de la vue précédente.
     */
    @Override
    public void onEnter(String fromView) {
        try {
            setView(view);
            view.setAuthorName(session.getAuthor());
            view.setIsbn(
                    ISBNFactory.computeISBNFor(LANG_CODE,
                            session
                                    .getMatricule()
                                    .substring(1),
                            getLastBookNumber() + 1).forUser());
        } catch(DataManipulationException e) {
            view.setMessage("Une erreur est survenue lors du chargement des données.", TypeMessage.ERROR);
        }
    }

    private int getLastBookNumber() {
        return Integer.parseInt(repo
                .getLastIsbn()
                .replaceAll("-", "")
                .substring(7, 9));
    }

    /**
     * Définit la vue avec laquelle le presenter va interagir.
     *
     * @param view  Vue avec laquelle interagir.
     */
    public void setView(CreateBookInterface view) {
        this.view = view;
    }

    /**
     * Retourne à la vue HomeView.
     */
    @Override
    public void onHomePressed() {
        view.goTo("HomeView");
    }
}
