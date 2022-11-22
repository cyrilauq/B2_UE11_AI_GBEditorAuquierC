package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.modeles.ExtendedBookDescription;
import org.helmo.gbeditor.modeles.LittleBookDescription;

/**
 * Définit les méthodes que HomeView exposera à son presenter, HomePresenter.
 */
public interface HomeInterface extends ViewInterface {

    /**
     * Ajoute un livre aux livres à afficher à la vue.
     *
     * @param bookDescription   La description du livre à ajouter.
     */
    void addBook(final LittleBookDescription bookDescription);

    /**
     * Affiche les détails du livre sélectionné.
     *
     * @param bookDescription La description détaillée du livre à afficher.
     */
    void setDetails(final ExtendedBookDescription bookDescription);

    /**
     * Vide la vue des livres qu'elle a.
     */
    void clearBooks();

    /**
     * Affiche la page sur laquelle l'utilisateur est actuellment.
     *
     * @param currentPage   Page courante.
     */
    void setCurrentPage(int currentPage);

    /**
     * Affiche les pages liées au livre sélectionné.
     *
     * @param num       Numéro de la page
     * @param content   Contenu de la page
     */
    void addAvailablePages(final int num, final String content);

}
