package org.helmo.gbeditor.modeles;

import java.util.Objects;

/**
 * Définit les informations qui seront données à la vue lorsqu'on voudra afficher un livre.
 */
public class LittleBookDescription {
    private static final String NON_PUBLISHED_STATE = "Non publié";
    private final String title;
    private final String imgPath;
    private final String author;
    private final String isbn;
    private String publishDate;

    /**
     * Créer une nouvelle LittleBookDescription ==> description non détaillée d'un livre.
     *
     * @param title     Titre du livre
     * @param imgPath   Chemin d'accès de l'image de couverture du livre
     * @param isbn      ISBN du livre
     * @param author    Auteur du livre
     */
    public LittleBookDescription(final String title, final String imgPath, final String isbn, final String author) {
        this.title = title;
        this.imgPath = imgPath;
        this.author = author;
        this.isbn = isbn;
    }

    /**
     * Créer une nouvelle LittleBookDescription ==> description non détaillée d'un livre.
     *
     * @param title     Titre du livre
     * @param imgPath   Chemin d'accès de l'image de couverture du livre
     * @param isbn      ISBN du livre
     * @param author    Auteur du livre
     */
    public LittleBookDescription(final String title, final String imgPath, final String isbn, final String author, final String publishDate) {
        this(title, imgPath, isbn, author);
        this.publishDate = publishDate;
    }

    public String getImgPath() {
        return imgPath;
    }

    /**
     * Récupère l'état de publication d'un livre.
     *
     * @return  La méthode renverra "Non publié" si <code>publishDate</code> vaut null, donc si le livre n'a pas encore été publié.
     *          Sinon, "Publié".
     */
    public String getPublishState() {
        return publishDate == null ? NON_PUBLISHED_STATE : "Publié";
    }

    /**
     * Détermine si un livre peut-être publié ou non.
     * Se base sur le fait que le livre ait une date de publication ou non.
     *
     * @return      True si la date de publication du livre est nulle.
     *              False si le livre a une date de publication.
     */
    public boolean canBePublished() {
        return publishDate == null;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title.substring(0, Math.min(title.length(), 50));
    }

    /**
     * Détermine si la descritpion à une image ou non.
     *
     * @return  True si l'image de la description n'est pas vide et non null.
     *          False sinon
     */
    public boolean hasImg() {
        return imgPath != null && !imgPath.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LittleBookDescription that = (LittleBookDescription) o;
        return title.equals(that.title) && imgPath.equals(that.imgPath) && author.equals(that.author) && isbn.equals(that.isbn);
    }

    @Override
    public String toString() {
        return "LittleBookDescription{" +
                "title='" + title + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publishDate='" + publishDate + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, imgPath, author, isbn, publishDate);
    }
}
