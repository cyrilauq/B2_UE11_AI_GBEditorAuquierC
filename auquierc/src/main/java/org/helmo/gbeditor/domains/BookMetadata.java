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
        attributes.put(BookFieldName.ISBN, ISBN.of(isbn).forUser());
        attributes.put(BookFieldName.SYS_ISBN, ISBN.of(isbn).toString());
        attributes.put(BookFieldName.AUTHOR, author == null ? "" : author);
        attributes.put(BookFieldName.SUMMARY, resume == null ? "" : resume);
        attributes.put(BookFieldName.TITLE, title == null ? "" : title);
        attributes.put(BookFieldName.PUBLISH_DATE, "");
    }

    public String getTitle() {
        return get(BookFieldName.TITLE);
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
        if(attribute == null || !attributes.containsKey(attribute)) {
            throw new FieldNotFoundException("Le champ chercher n'a pas été trouvé.");
        }
        var val = attributes.get(attribute);
        return val.isEmpty() ? null : val;
    }

    public String getPublishDate() { return get(BookFieldName.PUBLISH_DATE); }

    public void setPublishDate(final LocalDateTime publishDate) {
        attributes.put(BookFieldName.PUBLISH_DATE, publishDate == null ? "" : publishDate.format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm")));
    }

    public void publish() {
        attributes.put(BookFieldName.PUBLISH_DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm")));
    }

    public String getIsbn() { return get(BookFieldName.SYS_ISBN); }

    public String getSummary() {
        return get(BookFieldName.SUMMARY);
    }

    public String getAuthor() {
        return get(BookFieldName.AUTHOR);
    }

    public void setTitle(String title) {
        attributes.put(BookFieldName.AUTHOR, title);
    }

    public void setIsbn(String isbn) {
        attributes.put(BookFieldName.ISBN, new ISBN(isbn).forUser());
        attributes.put(BookFieldName.SYS_ISBN, new ISBN(isbn).toString());
    }

    public void setSummary(String resume) {
        attributes.put(BookFieldName.SUMMARY, resume);
    }

    @Override
    public String toString() {
        return "BookMetadata{" +
                "title='" + get(BookFieldName.TITLE) + '\'' +
                ", isbn=" + get(BookFieldName.ISBN) +
                ", summary='" + get(BookFieldName.SUMMARY) + '\'' +
                ", author='" + get(BookFieldName.AUTHOR) + '\'' +
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
