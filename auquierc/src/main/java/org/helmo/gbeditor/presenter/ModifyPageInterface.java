package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.modeles.ListChoiceItem;

import java.util.Collection;

/**
 * Définit les méthodes qui seront implémentées par la vue et qui permettront au presenter d'interagir avec elle.
 * Ces méthodes seront utiles pour la modification de pages.
 */
public interface ModifyPageInterface extends ViewInterface {

    /**
     * Affiche à l'écran de l'utilisateur le contenu de la page qu'il souhaite modifier.
     *
     * @param content   Contenu de la page en cours de modification.
     */
    void setPageContent(final String content);

    /**
     * Affiche à l'écran de l'utilisateur les choix déjà disponible pour la page courante.
     *
     * @param choices   Choix disponibles.
     */
    void setChoices(final Iterable<ListChoiceItem> choices);

    /**
     * Affiche à l'écran de l'utilisateur les choix possibles pour la page qu'il souhaite modifier.
     *
     * @param choices   Choix possibles.
     */
    void setTarget(final Iterable<ListChoiceItem> choices);

    /**
     * Affiche à l'écran les options disponibles pour l'ajout d'une page et affiche à l'écran le premier choix disponible.
     * Elle permet aussi de dire quel est le choix qui sera sélectionné par défaut.
     *
     * @param addOptions    Options d'ajout de page
     */
    void setMoveOptions(final Collection<String> addOptions, final String selectedOption);

    void showMoveTarget(final boolean show);

}
