package org.helmo.gbeditor.presenter;

/**
 * Définit les méthodes que les classes implémentant l'interface devront avoir.
 */
public interface CreateBookInterface extends ViewInterface {

    /**
     * Modifie le texte du résumé.
     * Cette méthode sera appelée seulement lorsque l'utilisateur aura entré un résumé qui sera trop long.
     *
     * @param txt   Résumé respectant la limite de caractères.
     */
    void setResumeTxt(final String txt);

    /**
     * Réinitialise les champs du formulaire de création de livre.
     */
    void resetInputs();

    /**
     * Affiche l'ISBN à l'écran.
     *
     * @param isbn  ISBN à afficher
     */
    void setIsbn(final String isbn);

}
