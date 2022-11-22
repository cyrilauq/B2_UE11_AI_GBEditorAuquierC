package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.modeles.ListChoiceItem;

import java.util.Iterator;
import java.util.List;

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

}
