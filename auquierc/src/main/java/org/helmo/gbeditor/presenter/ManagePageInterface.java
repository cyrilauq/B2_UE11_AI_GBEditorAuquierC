package org.helmo.gbeditor.presenter;

public interface ManagePageInterface extends ViewInterface {

    /**
     * Affiche une page à l'utilisateur.
     *
     * @param numPage   Numéro de la page à afficher.
     * @param content   Text de la page à afficher.
     */
    void addPage(final int numPage, final String content);

    /**
     * Supprime les pages affichées à l'utilisateur.
     */
    void clearPages();

}
