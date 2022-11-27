package org.helmo.gbeditor.domains;

import org.helmo.gbeditor.factory.BookFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * S'occupe des opérations faites sur un livre.
 *
 * @author  Cyril Auquier
 */
public class Book implements Iterable<Page> {
    private final BookMetadata data;
    private String imgPath;

    private final List<Page> pagesList = new LinkedList<>();

    /**
     * Crée un nuveau livre à partir d'un titre, un résumé, un auteur, un code ISBN et une image donné.
     *
     * @param bookMetadata  Meta data du livre.
     * @param imgPath   Chemin d'accès à la couverture du livre
     */
    public Book(final BookMetadata bookMetadata, final String imgPath) {
        this.data = bookMetadata;
        this.imgPath = imgPath;
    }

    /**
     * Récupère la valeur d'un attribut donné.
     *
     * @param attribute Attribut dont on cherche la valeur.
     *
     * @return          La valeur lièe à l'attribut recherché.
     *
     * @throws BookMetadata.FieldNotFoundException  Si l'attribut n'existe pas.
     */
    public String get(final BookFieldName attribute) { return data.get(attribute); }

    /**
     * Publie le livre en lui donnant comme date et heure de publication celles de l'instant présent.
     */
    public void publish() {
        if(data.get(BookFieldName.PUBLISH_DATE) != null) {
            throw new BookAlreadyPublishedException();
        }
        if(pagesList.isEmpty()) {
            throw new CannotPublishEmptyBookException();
        }
        data.set(BookFieldName.PUBLISH_DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm")));
    }

    /**
     * Définit à quelle date le livre a été publié.
     *
     * @param publishDate   Date à laquelle le livre a été publié.
     */
    public void setPublishDate(final LocalDateTime publishDate) {
        data.set(BookFieldName.PUBLISH_DATE, publishDate == null ? null : publishDate.format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm")));
    }

    /**
     * Modifie les données du livre en les raplaçant par celles données.
     *
     * @param data              Nouvelle donnée du livre.
     * @param authorMatricule   Matricule de l'auteur connecté.
     * @param imgPath           Chemin d'accès à l'image de couverture du livre.
     *
     * @throws CannotModifyPublishedBookException   Si le livre est publié.
     * @throws WrongFormattedBookException          Si les données fournies ne sont pas valide.
     */
    public void setNewData(final BookMetadata data, final String authorMatricule, final String imgPath) {
        if(this.data.get(BookFieldName.PUBLISH_DATE) != null) {
            throw new CannotModifyPublishedBookException();
        }
        var message = BookFactory.validBook(data.get(BookFieldName.TITLE),
                data.get(BookFieldName.SUMMARY), ISBN.isValid(data.get(BookFieldName.ISBN), authorMatricule));
        if(message != null) {
            throw new WrongFormattedBookException(message);
        }
        this.imgPath = imgPath;
        this.data.set(BookFieldName.SUMMARY, data.get(BookFieldName.SUMMARY));
        this.data.set(BookFieldName.TITLE, data.get(BookFieldName.TITLE));
        this.data.set(BookFieldName.ISBN, data.get(BookFieldName.ISBN));
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
     * Crée un nouveau livre à partir d'un titre, un résumé, un auteur et un code ISBN donné.
     *
     * @param bookMetadata  Meta data du livre.
     */
    public Book(final BookMetadata bookMetadata) {
        this(bookMetadata, "");
    }

    public String getImgPath() {
        return imgPath;
    }

    /**
     * Détermine si la page donnée est liée ou non à une ou plusieurs autres pages.
     *
     * @param page  Page
     *
     * @return      Si la page est liée à au moins une autre page, renvoie true.
     *              Si la page n'a pas de lien avec d'autres pages, renvoie false.
     */
    public boolean pageIsATarget(final Page page) {
        var count = 0;
        for(final var p : this) {
            for(final var c : p) {
                if(p.getPageForChoice(c).equals(page)) {
                    count++;
                }
            }
        }
        return count != 0;
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
    private boolean containsPage(final Page page) {
        return pagesList.contains(page);
    }

    /**
     * Supprime une page donnée du livre.
     * Supprime aussi les liens de la page avec d'autres pages du livre.
     *
     * @param toRemove  Page à supprimer.
     *
     * @return          True si la page était présente dans le livre et si après suppression elle n'est plus présente dans le livre.
     *                  False si la page n'était pas présente dans le livre ou si elle est toujours présente après sa suppression.
     */
    public boolean removePage(final Page toRemove) {
        forEach(p -> p.remove(toRemove));
        return pagesList.remove(toRemove) && !containsPage(toRemove);
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
        return this.data.get(BookFieldName.ISBN).equals(that.data.get(BookFieldName.ISBN));
    }

    @Override
    public int hashCode() {
        return data.get(BookFieldName.ISBN).hashCode();
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
     * Définit une exception qui sera lancée quand l'utilisateur tentera de publié un livre qui ne contient pas de page.
     */
    public static class CannotPublishEmptyBookException extends RuntimeException {
        /**
         * Crée une nouvelle CannotPublishEmptyBookException.
         */
        public CannotPublishEmptyBookException() {
            super("Un livre publié si il ne possède pas de page.");
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

}
