package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.*;
import org.helmo.gbeditor.domains.factory.ISBNFactory;
import org.helmo.gbeditor.repositories.exceptions.BookAlreadyExistsException;
import org.helmo.gbeditor.repositories.DataRepository;

/**
 * Cette classe s'occupe de la logique qui se cache derrière les actions de la vue CreateBookView.
 * // TODO : Rendre l'ISBN modificable
 * @author  Cyril Auquier
 */
public class ModifyBookPresenter extends Presenter {
    private final Session session;

    private final DataRepository repo;
    private ModifyBookInterface view;

    private Book currentBook;

    private static final int LANG_CODE = 2;

    /**
     * Crée un nouveau CreateBookPresenter à partir d'une Session et d'un DataReoisutory donné.
     *
     * @param session   Session courante.
     * @param repo      Repository courant.
     */
    public ModifyBookPresenter(final Session session, final DataRepository repo) {
        this.repo = repo;
        this.session = session;
    }

//    private Book getBookFor(final BookMetadata bookMetadata, String authorMatricul, final String filePath) {
//        if(filePath == null || filePath.isBlank()) {
//            return Book.of(bookMetadata, authorMatricul);
//        }
//        return Book.of(bookMetadata, authorMatricul,
//                FileUtils.copyFile(filePath,
//                        Paths.get(System.getProperty("user.home"), "ue36", "e200106").toAbsolutePath().toString(),
//                        bookMetadata.getIsbn()
//                )
//        );
//    }

    /**
     * Crée un nouveau livre à partir d'un titre, un code ISBN et un résumé donné.
     * Si un de ces champs est vide ou jugé non valide, la création du livre sera annulée.
     *
     * @param title     Titre du livre.
     * @param isbn      Code ISBN du livre.
     * @param resume    Résumé du livre.
     * @param filePath  Chemin d'accès de l'image de couverture.
     */
    public void modifyBook(final String title, final String isbn, final String resume, final String filePath) {
        try {
            currentBook.setNewData(
                    new BookMetadata(title, isbn, resume, session.getAuthor()),
                    session.getMatricule().substring(1),
                    filePath);
            repo.save(currentBook);
            view.resetInputs();
            view.setIsbn(
                    ISBNFactory.computeISBNFor(LANG_CODE,
                            session.getMatricule().substring(1),
                            (getLastBookNumber()) + 1).forUser());
            setMessage("Le livre a bien été modifié.");
            view.refreshAll(ViewName.MODIFY_BOOK_VIEW);
        } catch (BookAlreadyExistsException | Book.WrongFormattedBookException | ISBN.WrongFormattedISBNException e) {
            setMessage(e.getMessage());
        }
    }

    private void setMessage(final String message) {
        view.setMessage(message, TypeMessage.MESSAGE);
    }

    /**
     * Définit les actions réalisées par le presenter lorsque la vue fera appel à lui.
     *
     * @param fromView  Nom de la vue précédente.
     */
    @Override
    public void onEnter(String fromView) {
        currentBook = repo.searchBookFor(session.getCurrentIsbn());
        setView(view);
        view.setAuthorName(session.getAuthor());
        view.setIsbn(currentBook.get(BookFieldName.ISBN));
        view.setTitle(currentBook.get(BookFieldName.TITLE));
        view.setImg(currentBook.getImgPath());
        view.setResume(currentBook.get(BookFieldName.SUMMARY));
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
    public void setView(final ModifyBookInterface view) {
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
