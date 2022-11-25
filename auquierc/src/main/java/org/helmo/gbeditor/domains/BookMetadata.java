package org.helmo.gbeditor.domains;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Cette classe permet de stocker les informations générales d'un livre: titre, auteur, isbn et résumé.
 *
 * @author cyril
 */
public class BookMetadata {
    private String title;
    private ISBN isbn;
    private String summary;
    private final String author;
    private LocalDateTime publishDate;
    private Map<BookFieldName, String> attributes = new TreeMap<>();

    /**
     * Crée un nouvel objet BookMetaData sur base d'infos données.
     *
     * @param title     Titre du livre
     * @param isbn      ISBN du livre
     * @param resume    Résumé du livre
     * @param author    Auteur du livre
     */
    public BookMetadata(final String title, final String isbn, final String resume, final String author) {
        this.title = title;
        this.isbn = ISBN.of(isbn);
        this.summary = resume;
        this.author = author;
    }

    /**
     * Crée un nouvel objet BookMetaData sur base d'infos données.
     *
     * @param title     Titre du livre
     * @param isbn      ISBN du livre
     * @param resume    Résumé du livre
     * @param author    Auteur du livre
     */
    public BookMetadata(final String title, final String isbn, final String resume, final String author, final LocalDateTime publishDate) {
        this.title = title;
        this.isbn = ISBN.of(isbn);
        this.summary = resume;
        this.author = author;
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Récupère la valeur de l'attribut recherché.
     *
     * @param attribute     Attribut recherché.
     *
     * @return              La valeur de l'attribut recherché.
     *
     * @throws FieldNotFoundException si l'attribut recherché n'existe pas.
     */
    public String get(final BookFieldName attribute) {
        switch (attribute) {
            case TITLE:
                return title;
            case AUTHOR:
                return author;
            case SUMMARY:
                return summary;
            case ISBN:
                return isbn.forUser();
            case SYS_ISBN:
                return isbn.toString();
            case PUBLISH_DATE:
                return publishDate == null ? null : publishDate.format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm"));
            default:
                throw new FieldNotFoundException("Le champ chercher n'a pas été trouvé.");
        }
    }

    public LocalDateTime getPublishDate() { return publishDate; }

    public void setPublishDate(final LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public void publish() {
        publishDate = LocalDateTime.now();
    }

    public String getIsbn() { return isbn.toString(); }

    public String getSummary() {
        return summary;
    }

    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = new ISBN(isbn);
    }

    public void setSummary(String resume) {
        this.summary = resume;
    }

    /**
     * Crée une copie identique à un objet BookMetaData donné.
     *
     * @param toCopy    Objet BookMetaData qu'on souhaite copier.
     *
     * @return          La copie conforme du BookMetaData donné.
     */
    public static BookMetadata copyOf(BookMetadata toCopy) {
        return new BookMetadata(toCopy.title, toCopy.isbn.toString(), toCopy.summary, toCopy.author);
    }

    @Override
    public String toString() {
        return "BookMetadata{" +
                "title='" + title + '\'' +
                ", isbn=" + isbn +
                ", summary='" + summary + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    /**
     * Cette classe instancie une exception qui sera lancée quand on cherchera à récupérer un champ qui n'existe pas.
     */
    public static class FieldNotFoundException extends RuntimeException {

        /**
         * Crée une nouvelle FieldNotFoundException avec un message donné.
         *
         * @param message   Message de l'exception.
         */
        public FieldNotFoundException(final String message) {
            super(message);
        }

    }
}
