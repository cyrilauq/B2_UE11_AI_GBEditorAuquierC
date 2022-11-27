package org.helmo.gbeditor.presenter;

/**
 * Définit les méthodes qui seront exposées pour pouvoir interagir avec une BookDescriptionView.
 * TODO : Réfléchir à un autre nom pour cette interface
 */
public interface BookDescriptionEventHandler {

    /**
     * Affiche les détails d'un livre portant l'ISBN donné.
     *
     * @param isbn  ISBN du livre qu'on cherche à afficher.
     */
    void displayDetailsFor(final String isbn);
}
