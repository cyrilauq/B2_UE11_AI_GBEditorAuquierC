package org.helmo.gbeditor.presenter;

/**
 * Expose une méthode qui sera utile pour permettre à l'utilisateur de supprimer un choix.
 */
public interface ChoiceViewEventHandler {

    /**
     * Réagit à l'action "Confirmer la suppression".
     *
     * @param content   Contenu de la page à supprimer.
     */
    void onConfirmedDelete(final String content);

}
