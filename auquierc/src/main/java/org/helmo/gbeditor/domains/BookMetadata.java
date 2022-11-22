package org.helmo.gbeditor.domains;

import java.time.LocalDateTime;

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
}
