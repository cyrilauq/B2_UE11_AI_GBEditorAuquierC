package org.helmo.gbeditor.presenter.viewmodels;

import org.helmo.gbeditor.domains.Page;

import java.util.*;

/**
 * Définit les informations qui seront données à la vue lorsqu'on voudra afficher les détails d'un livre.
 */
public class ExtendedBookDescription implements Iterable<PageBookDescription> {
    private final String resume;
    private final LittleBookDescription littleBookDescription;
    private final List<PageBookDescription> pages = new ArrayList<>();

    /**
     * Créer une nouvelle ExtendedBookDescription ==> descritpion détaillée d'un livre.
     *
     * @param littleBookDescription Objet LittleBookDescription représentant une petite description du livre
     * @param resume                Résumé du livre
     */
    public ExtendedBookDescription(LittleBookDescription littleBookDescription, final String resume) {
        this.littleBookDescription = littleBookDescription;
        this.resume = resume;
    }

    /**
     * Créer une nouvelle ExtendedBookDescription ==> descritpion détaillée d'un livre.
     *
     * @param littleBookDescription Objet LittleBookDescription représentant une petite description du livre
     * @param resume                Résumé du livre
     */
    public ExtendedBookDescription(LittleBookDescription littleBookDescription, final String resume, Iterator<Page> pages) {
        this.littleBookDescription = littleBookDescription;
        this.resume = resume;
        addPages(pages);
    }

    private void addPages(Iterator<Page> pages) {
        int count = 0;
        while(pages.hasNext()) {
            count++;
            this.pages.add(new PageBookDescription(count, pages.next().getContent()));
        }
    }

    /**
     * Récupère le chemin d'accès à l'image de couverture du livre de la description.
     *
     * @return  Le chemin d'accès à l'image de couverture du livre de la description.
     */
    public String getImgPath() {
        return littleBookDescription.getImgPath();
    }

    /**
     * Retourne la date de publication du livre.
     *
     * @return  La date de publication du livre.
     *          Exemple: Le 20-11-22 à 10:10.
     */
    public String getPublishDate() {
        return littleBookDescription.getPublishState();
    }

    /**
     * Détermine si un livre peut-être publié ou non.
     * Se base sur le fait que le livre ait une date de publication ou non.
     *
     * @return      True si la date de publication du livre est nulle.
     *              False si le livre a une date de publication.
     */
    public boolean canBePublished() {
        return littleBookDescription.canBePublished();
    }

    /**
     * Récupère le resume du livre de la description.
     *
     * @return  Résumé du livre de la description.
     */
    public String getResume() {
        return resume;
    }

    /**
     * Récupère le ISBN du livre de la description.
     *
     * @return  ISBN du livre de la description.
     */
    public String getIsbn() {
        return littleBookDescription.getIsbn();
    }

    /**
     * Récupère le auteur du livre de la description.
     *
     * @return  Auteur du livre de la description.
     */
    public String getAuthor() {
        return littleBookDescription.getAuthor();
    }

    /**
     * Récupère le titre du livre de la description.
     *
     * @return  Titre du livre de la description.
     */
    public String getTitle() {
        return littleBookDescription.getTitle();
    }

    /**
     * Détermine si la description à une image ou non.
     *
     * @return  True si l'image de la description n'est pas vide et non null.
     *          False sinon
     */
    public boolean hasImg() { return littleBookDescription.hasImg(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtendedBookDescription that = (ExtendedBookDescription) o;
        return littleBookDescription.equals(that.littleBookDescription) && this.resume.equals(that.resume);
    }

    @Override
    public String toString() {
        return "ExtendedBookDescription{" +
                "resume='" + resume + '\'' +
                ", littleBookDescription=" + littleBookDescription +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(resume, littleBookDescription);
    }

    @Override
    public Iterator<PageBookDescription> iterator() {
        return pages.iterator();
    }
}
