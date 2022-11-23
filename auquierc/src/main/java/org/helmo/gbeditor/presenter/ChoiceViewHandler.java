package org.helmo.gbeditor.presenter;

public interface ChoiceViewHandler {

    /**
     * Réagit à l'action "Confirmer la suppression".
     *
     * @param content   Contenu de la page à supprimer.
     */
    void onConfirmedDelete(final String content);

}
