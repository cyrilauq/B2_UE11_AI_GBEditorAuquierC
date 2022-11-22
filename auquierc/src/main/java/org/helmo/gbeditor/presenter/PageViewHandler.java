package org.helmo.gbeditor.presenter;

public interface PageViewHandler {

    /**
     * Permet de réagir à l'action "Modifier page".
     *
     * @param content   Contenu de la page à modifier.
     */
    void onEdit(final String content);

    /**
     * Réagit à l'action "Confirmer la suppression".
     *
     * @param content   Contenu de la page à supprimer.
     */
    void onConfirmedDelete(final String content);

}
