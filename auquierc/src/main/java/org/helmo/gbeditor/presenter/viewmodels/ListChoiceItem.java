package org.helmo.gbeditor.presenter.viewmodels;

/**
 * Cette classe représente les informations qui seront utilisées pour l'affichage d'un choix.
 */
public class ListChoiceItem {
    private final String content;
    private final int numPage;
    private String label;

    /**
     * Crée un nouveau ListChoiceItem avec le numéro et le contenu, d'une page cible, donné.
     *
     * @param numPage   Numéro de page de la page cible du choix
     * @param content   Contenu de la page cible.
     */
    public ListChoiceItem(final int numPage, final String content) {
        this.numPage = numPage;
        this.content = content;
    }

    /**
     * Crée un nouveau ListChoiceItem avec le numéro et le contenu, d'une page cible, donné.
     *
     * @param numPage   Numéro de page de la page cible du choix.
     * @param content   Contenu de la page cible.
     * @param label     Label du choix.
     */
    public ListChoiceItem(final String label, final int numPage, final String content) {
        this(numPage, content);
        this.label = label;
    }

    public int getNumPage() {
        return numPage;
    }

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }
}
