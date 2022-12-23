package org.helmo.gbeditor.presenter;

/**
 * Définit les méthodes qui seront implémentées par la vue et qui permettront au presenter d'interagir avec elle.
 * Ces méthodes seront utiles pour la gestion de pages.
 */
public interface ManagePageInterface extends ViewInterface {

    /**
     * Affiche une page à l'utilisateur.
     *
     * @param numPage   Numéro de la page à afficher.
     * @param content   Text de la page à afficher.
     * @param nPageBranchment   Détermine si la page est la destination de choix ou non.
     */
    void addPage(final int numPage, final String content, final int nPageBranchment);

    /**
     * Supprime les pages affichées à l'utilisateur.
     */
    void clearPages();

}
