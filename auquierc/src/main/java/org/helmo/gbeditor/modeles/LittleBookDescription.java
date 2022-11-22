package org.helmo.gbeditor.modeles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Définit les informations qui seront données à la vue lorsqu'on voudra afficher un livre.
 */
public class LittleBookDescription {
    private final String title;
    private final String imgPath;
    private final String author;
    private final String isbn;
    private LocalDateTime publishDate;

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
        this.publishDate = publishDate;
    }

    /**
     * Créer une nouvelle LittleBookDescription ==> description non détaillée d'un livre.
     *
     * @param title     Titre du livre
     * @param imgPath   Chemin d'accès de l'image de couverture du livre
     * @param isbn      ISBN du livre
     * @param author    Auteur du livre
     */
    public LittleBookDescription(final String title, final String imgPath, final String isbn, final String author, final LocalDateTime publishDate) {
        this(title, imgPath, isbn, author);
        this.publishDate = publishDate;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getPublishDate() {
        return publishDate == null ? "Non publié" : "Le " + publishDate.format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm"));
    }

    public boolean canBePublished() {
        return publishDate != null;
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
}
