package org.helmo.gbeditor.domains;

import org.helmo.gbeditor.infrastructures.jdbc.ConnectionFailedException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * S'occupe des opérations faites sur un livre.
 *
 * @author  Cyril Auquier
 */
public class Book implements Iterable<Page> {

    private final BookMetadata data;
    private LocalDateTime datePublication;
    private String imgPath;

    private final List<Page> pagesList = new LinkedList<>();

    /**
     * Crée un nuveau livre à partir d'un titre, un résumé, un auteur, un code ISBN et une image donné.
     *
     * @param bookMetadata  Meta data du livre.
     * @param imgPath   Chemin d'accès à la couverture du livre
     */
    public Book(final BookMetadata bookMetadata, final String imgPath) {
        this.data = BookMetadata.copyOf(bookMetadata);
        this.imgPath = imgPath;
    }

    private void setTitle(final String title) {
        data.setTitle(title);
    }

    private void setSummary(final String summary) {
        data.setSummary(summary);
    }

    private void setIsbn(final String isbn) {
        data.setIsbn(isbn);
    }

    /**
     * Publie le livre en lui donnant comme date et heure de publication celles de l'instant présent.
     */
    public void publish() {
        if(data.getPublishDate() != null) {
            throw new BookAlreadyPublishedException();
        }
        data.publish();
    }

    public void setPublishDate(final LocalDateTime publishDate) {
        data.setPublishDate(publishDate);
    }

    public LocalDateTime getPublishDate() {
        return data.getPublishDate();
    }

    public void setNewData(final BookMetadata data, final String authorMatricule, final String imgPath) {
        if(this.data.getPublishDate() != null) {
            throw new CannotModifyPublishedBookException();
        }
        var message = validBook(data.getTitle(), data.getSummary(), ISBN.isValid(data.getIsbn(), authorMatricule));
        if(message != null) {
            throw new WrongFormattedBookException(message);
        }
        this.imgPath = imgPath;
        setSummary(data.getSummary());
        setTitle(data.getTitle());
        setIsbn(data.getIsbn());
    }

    /**
     * Récupère le numéro de page d'une page donnée.
     *
     * @param page  Page dont on veut le numéro.
     *
     * @return      Numéro de la page.
     *             0 si la page n'est pas présente dans le livre.
     */
    public int getNPageFor(final Page page) {
        return indexOf(page) + 1;
    }

    /**
     * Ajoute une page donnée au début du livre.
     *
     * @param page  Page à ajouter.
     */
    public void addBegin(final Page page) {
        if(page == null || containsPage(page)) { return; }
        if(pagesList.isEmpty()) {
            pagesList.add(page);
        } else {
            pagesList.add(0, page);
        }
    }

    /**
     * Ajoute une page donnée à la fin du livre.
     *
     * @param page  Page à ajouter.
     */
    public void addEnd(final Page page) {
        if(page == null || containsPage(page)) { return; }
        pagesList.add(page);
    }

    /**
     * Ajoute une page donnée après une autre page donnée.
     *
     * @param page  Page à ajouter.
     * @param after Page après laquelle l'autre page va s'ajouter.
     */
    public void addAfter(final Page page, final Page after) {
        if(page == null || containsPage(page) || after == null) { return; }
        int index = indexOf(after);
        pagesList.add(index + 1, page);
    }

    private int indexOf(final Page page) {
        return pagesList.indexOf(page);
    }

    /**
     * Crée un nuveau livre à partir d'un titre, un résumé, un auteur et un code ISBN donné.
     *
     * @param bookMetadata  Meta data du livre.
     */
    public Book(final BookMetadata bookMetadata) {
        this(bookMetadata, "");
    }

    /**
     * Crée un nuveau livre à partir d'un titre, un résumé, un auteur et un code ISBN donné.
     *
     * @param bookMetadata  Meta data du livre.
     */
    public Book(final BookMetadata bookMetadata, final List<Page> pages) {
        this(bookMetadata, "");
    }

    /**
     * Ajoute une nouvelle page au livre si celle-ci n'est pas nulle.
     *
     * @param page  Page à ajouter au livre.
     */
    public void addPage(final Page page) {
        if(page == null) { return; }
        pagesList.add(page);
    }

    /**
     * Trouve et retourne la première page du livre.
     *
     * @return  La première page du livre ou null si le livre ne contient aucune page.
     */
    public Page findFirstPage() {
        return pagesList.isEmpty() ? null : pagesList.get(0);
    }

    /**
     * Trouve et retourne la dernière page du livre.
     *
     * @return  La dernière page du livre ou null si le livre ne contient aucune page.
     */
    public Page findLastPage() {
        return pagesList.get(pagesList.size() - 1);
    }

    public String getImgPath() {
        return imgPath;
    }

    /**
     * Récupère le titre du livre.
     *
     * @return  Le titre du livre.
     */
    public String getTitle() { return data.getTitle(); }

    /**
     * Récupère le résumé du livre.
     *
     * @return  Le résumé du livre.
     */
    public String getResume() { return data.getSummary(); }

    /**
     * Récupère l'auteur du livre.
     *
     * @return  L'auteur du livre.
     */
    public String getAuthor() { return data.getAuthor(); }

    /**
     * Récupère l'ISBN du livre.
     *
     * @return  L'ISBN du livre.
     */
    public String getIsbn() { return data.getIsbn(); }

    /**
     * Crée un livre sur base des informations données et vérifie que ces données soient correctes et cohérentes.
     * Elle doivent être non null et non vide.
     *
     * @param metadata          Les données générales du livre.
     * @param authorMatricule   Le matricule de l'auteur du livre.
     * @param filePath          Le chemin d'accès à l'image de couverture du livre.
     *
     * @return                  Un livre correctement formatté.
     *                          C'est à dire avec:
     *                              - un titre non null et non vide
     *                              - un auteur non null et non vide
     *                              - un isbn non null et non vide
     *                              - un résumé non null et non vide
     *                          Si les infos données ne sont pas valide la méthode renvoie une WrongFormattedBookException.
     */
    public static Book of(final BookMetadata metadata, String authorMatricule, final String filePath) {
        var message = validBook(metadata.getTitle(), metadata.getSummary(), ISBN.isValid(metadata.getIsbn(), authorMatricule));
        if(message != null) {
            throw new WrongFormattedBookException(message);
        }
        return new Book(
                metadata,
                filePath
        );
    }

    /**
     * Crée un livre sur base des informations données et vérifie que ces données soient correctes et cohérentes.
     * Elle doivent être non null et non vide.
     *
     * @param metadata          Les données générales du livre.
     * @param authorMatricule   Le matricule de l'auteur du livre.
     *
     * @return                  Un livre correctement formatté.
     *                          C'est à dire avec:
     *                              - un titre non null et non vide
     *                              - un auteur non null et non vide
     *                              - un isbn non null et non vide
     *                              - un résumé non null et non vide
     *                          Si les infos données ne sont pas valide la méthode renvoie une WrongFormattedBookException.
     */
    public static Book of(final BookMetadata metadata, String authorMatricule) {
        var message = validBook(metadata.getTitle(), metadata.getSummary(), ISBN.isValid(metadata.getIsbn(), authorMatricule));
        if(message != null) {
            throw new WrongFormattedBookException(message);
        }
        return new Book(
                metadata
        );
    }

    private static String validBook(String title, String resume, String validIsbn) {
        if(title == null || title.isBlank()) {
            return BookTypeError.EMPTY_TITLE.getMessage();
        } else if (resume == null || resume.isBlank()) {
            return BookTypeError.EMPTY_RESUME.getMessage();
        }
        return verifyContent(title, resume, validIsbn);
    }

    /**
     * Vérifie qu'un livre a le même ISBN que celui donné.
     *
     * @param isbn  L'ISBN qu'on cherche dans le livre.
     *
     * @return      Si le livre a un ISBN identique a celui donné, elle renvoie false.
     *              Sinon renvoie false.
     */
    public boolean hasIsbn(final String isbn) {
        return data.getIsbn().replace("-", "").equals(isbn.replace("-", ""));
    }

    /**
     * Calcule le numéro de page de la page donnée.
     *
     * @param page  La page dont on cherche le numéro.
     *
     * @return      Le numéro de la page donnée ou 0 si la page n'est pas dans le livre.
     */
    public int getNForPage(final Page page) {
        return indexOf(page) + 1;
    }

    /**
     * Récupère la page du livre qui a le contenu donné.
     *
     * @param content   Le contenu de la page que l'on cherche.
     *
     * @return          La page qui a le contenu donné ou null si aucune page ne correspond.
     */

    public Page getPageFor(final String content) {
        return pagesList.stream()
                .filter(p -> p.getContent().equalsIgnoreCase(content))
                .findFirst()
                .orElse(null);
    }

    /**
     * Détermine si une page donnée est présente dans le livre.
     *
     * @param page  Page cherchée.
     *
     * @return      True si la page est présente dans le livre.
     *              False, si la page n'est pas présente dans le livre.
     */
    public boolean containsPage(final Page page) {
        return pagesList.contains(page);
    }

    private static String verifyContent(final String title, final String resume, final String validIsbn) {
        if(title.length() > 150) {
            return BookTypeError.TITLE_TOO_LONG.getMessage();
        } else if(resume.length() > 500) {
            return BookTypeError.RESUME_TOO_LONG.getMessage();
        }
        return validIsbn;
    }

    public int pageCount() {
        return pagesList.size();
    }

    /**
     * Supprime une page donnée du livre.
     * Supprime aussi les liens de la page avec d'autres pages du livre.
     *
     * @param toRemove  Page à supprimer.
     */
    public void removePage(final Page toRemove) {
        forEach(p -> p.remove(toRemove));
        pagesList.remove(toRemove);
    }

    @Override
    public String toString() {
        return "Book{" +
                "data=" + data +
                ", imgPath='" + imgPath + '\'' +
                ", pages=" + pagesList +
                ", nbr pages=" + pagesList.size() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) { return true; }
        if(!(obj instanceof Book)) { return false; }
        Book that = (Book) obj;
        return this.data.getIsbn().equals(that.data.getIsbn());
    }

    @Override
    public int hashCode() {
        return data.getIsbn().hashCode();
    }

    @Override
    public Iterator<Page> iterator() {
        return pagesList.iterator();
    }

    /**
     * Définit une exception qui sera lancée si le format du livre n'est pas bon.
     * C'est-à-dire, si une des informations le composant est nulle ou vide.
     */
    public static class WrongFormattedBookException extends RuntimeException {

        /**
         * Crée une nouvelle exception liée au format du livre.
         *
         * @param message Message de l'erreur.
         */
        public WrongFormattedBookException(String message) {
            super(message);
        }
    }

    /**
     * Définit une exception qui sera lancée quand l'utilisateur tentera de modifier un livre publié.
     */
    public static class CannotModifyPublishedBookException extends RuntimeException {
        /**
         * Crée une nouvelle CannotModifyPublishedBookException.
         */
        public CannotModifyPublishedBookException() {
            super("Un livre publié ne peut pas être modifié.");
        }
    }

    /**
     * Définit une exception qui sera lancée quand l'utilisateur tentera de publié un livre déjà publié.
     */
    public static class BookAlreadyPublishedException extends RuntimeException {
        /**
         * Crée une nouvelle BookAlreadyPublishedException.
         */
        public BookAlreadyPublishedException() {
            super("Le livre a déjà été publié.");
        }
    }

    /**
     * Enumère les différents types d'erreurs possible lors de la création d'un livre.
     * Elle permettra, entre autre, de ne pas avoir à modifier les tests si les messages doivent changer.
     *
     * @author cyril
     */
    public enum BookTypeError {
        TITLE_TOO_LONG("La longueur du titre ne peut pas être supérieur à 150 caractères."),
        RESUME_TOO_LONG("La longueur du résumé ne peut pas être supérieur à 500 caractères."),
        EMPTY_RESUME("Le champ du résumé ne peut pas être vide."),
        EMPTY_TITLE("Le champ du titre ne peut pas être vide.");

        private final String message;

        BookTypeError(String message) { this.message = message; }

        public String getMessage() {
            return message;
        }
    }

}
