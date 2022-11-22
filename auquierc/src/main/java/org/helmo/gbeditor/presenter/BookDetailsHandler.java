package org.helmo.gbeditor.presenter;

// TODO : Réfléchir à un nom plus parlant
/*
 * Cette interface s'occupe d'initialiser les méthodes permettant de réagir aux interactions de l'utilisateur avec les details d'un livre.
 */
public interface BookDetailsHandler {

    /*
     * Permet à l'utilisateur publier un livre ayant l'ISBN donné.
     *
     * @param isbn  ISBN du livre à publier.
     */
    void onPublishBook(final String isbn);

    /*
     * Permet à l'utilisateur de modifier un livre ayant l'ISBN donné.
     *
     * @param isbn  ISBN du livre à modifier.
     */
    void onModifyBook(final String isbn);

    /**
     * Réagit au clic du bouton permettant de gérer les pages d'un livre ayant l'ISBN donné.
     * Permet à l'utilisateur d'aller sur la page de gestion des pages du livre.
     *
     * @param isbn  ISBN du livre dont on souhaite gérer les pages.
     */
    void onManagePages(final String isbn);

}
