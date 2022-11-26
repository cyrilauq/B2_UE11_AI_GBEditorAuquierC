package org.helmo.gbeditor.presenter;

import java.util.Collection;

/**
 * Déclare les méthodes qui seront utiles pour pouvoir afficher les informations necessaire à la création de page.
 */
public interface CreatePageInterface extends ViewInterface {

    /**
     * Ajoute une page dans la vue.
     *
     * @param pageContent   Contenu de la page à afficher.
     */
    void addBookPages(final int numPages, final String pageContent);

    /**
     * Vide les pages affichées dans la vue.
     */
    void clearBookPages();

    /**
     * Affiche un message donné à l'utilisateur.
     *
     * @param message   Message à afficher à l'utilisateur
     */
    void setMessage(final String message);

    /**
     * Affiche à l'écran les options disponibles pour l'ajout d'une page et affiche à l'écran le premier choix disponible.
     * Elle permet aussi de dire quel est le choix qui sera sélectionné par défaut.
     *
     * @param addOptions    Options d'ajout de page
     */
    void setAddOptions(final Collection<String> addOptions, final String selectedOption);

    /**
     * Affiche ou cache le choix de page en fonction de la valeur de show.
     * Si show vaut true, alors, la méthode affiche le choix de page, si il est à false, le choix de page est masqué.
     * @param show
     */
    void showBookPages(final boolean show);

}
