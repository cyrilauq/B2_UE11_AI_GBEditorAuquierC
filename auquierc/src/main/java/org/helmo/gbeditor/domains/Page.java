package org.helmo.gbeditor.domains;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Cette classe s'occupe de l'instanciation d'une page et des opérations possibles dessus.
 */
public class Page implements Iterable<String>, Comparable<Page> {
    private String content;
    private final Map<String, Page> choices = new HashMap<>();

    /**
     * Crée une nouvelle page avec un contenu donné.
     *
     * @param content   Contenu de la page.
     */
    public Page(final String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Page> getChoices() {
        return Map.copyOf(choices);
    }

    /**
     * Récupère la page liée au choix donné.
     *
     * @param choice    Choix correspondant à la page que l'on souhaite récupérer.
     *
     * @return          La page associée au choix donnée ou null si les choix n'existe pas dans la map de choix.
     */
    public Page getPageForChoice(String choice) {
        return choices.get(choice);
    }

    /**
     * Supprime une page donnée des choix de la page.
     *
     * @param page  Page à supprimer de choix.
     */
    public void remove(final Page page) {
        choices.values().remove(page);
    }

    /**
     * Ajouter un choix donné avec une page cible donnée à la page.
     *
     * @param choice    Libellé du choix.
     * @param target    Page cible du choix.
     *
     * @throws IllegalArgumentException Si la cible du choix est nulle ou si c'est la méme page que la page courante.
     */
    public void addChoice(final String choice, final Page target) {
        if(target == this) {
            throw new IllegalArgumentException("Une page ne peut pas être liée à elle-même.");
        }
        if(choice != null && !choice.isEmpty() && target != null) {
            choices.put(choice, target);
        }
    }

    /**
     * Défénit quels seront les choix disponibles pour la page.
     *
     * @param choices   Choix que la page aura.
     */
    public void setChoices(final Map<String, Page> choices) {
        this.choices.putAll(choices);
    }

    /**
     * Supprime un choix donné de la page.
     *
     * @param choice    Choix à supprimer.
     */
    public void removeChoice(final String choice) {
        choices.remove(choice);
    }

    public String getContent() {
        return content;
    }

    // TODO : Modification page ==> afficher boutton pour bouger la page
    // TODO : Afficher les choix pour la place de la page

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Page page = (Page) o;
        return this.content.equalsIgnoreCase(page.content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "Page{" +
                "content='" + content + "'" +
                ", choices=" + choices + "}";
    }

    @Override
    public Iterator<String> iterator() {
        return choices.keySet().iterator();
    }

    @Override
    public int compareTo(Page o) {
        return getContent().compareTo(o.content);
    }
}
