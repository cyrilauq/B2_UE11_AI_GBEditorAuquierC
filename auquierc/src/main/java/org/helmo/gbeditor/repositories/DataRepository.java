package org.helmo.gbeditor.repositories;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;

import java.util.List;

/**
 * Définit les méthodes que les classes implémentant l'interface devront exposer.
 * Ces méthodes seront utiles pour la récupération de livre depuis différentes ressources.
 */
public interface DataRepository {

    /**
     * Récupère les livres créés par l'auteur courant.
     *
     * @return      Une liste de BookDTO si des livres ont été trouvés.
     *              Une liste vide si aucun livre n'a été trouvé.
     */
    List<BookDTO> getData();

    /**
     * Définit l'auteur actuellement connecté.
     *
     * @param author    Auteur actuellement connecté.
     */
    void setCurrentAuthor(final String author);

    /**
     * Ajoute un ou plusieurs livres aux livres créés.
     *
     * @param books     Le ou les livre(s) à ajouter.
     */
    void add(Book... books);

    /**
     * Sauvegarde tous les livres contenu dans le repository.
     */
    void save(final Book book);

    /**
     * Supprime un ou plusieurs livres aux livres créés.
     *
     * @param books     Le ou les livre(s) à supprimer.
     *
     * @return          True si les livres ont été supprimés.
     *                  False s'ils n'ont pas pu être supprimés.
     */
    boolean remove(String... books);

    /**
     * Retourne les livres qui ont été récupérés.
     *
     * @return      Une liste de Book.
     */
    List<Book> getBooks();

    /**
     * Récupère et stocke les livres de l'auteur courant.
     */
    void loadBooks();

    /**
     * Récupère l'ISBN du dernier livre ajouté.
     *
     * @return  Une chaine de caractères représentant l'ISBN du dernier livre ajouté.
     */
    String getLastIsbn();

    /**
     * Récupère le livre ayant pour ISBN celui donnée.
     *
     * @param isbn  ISBN du livre à chercher
     *
     * @return      Si le livre n'est pas trouvé, retourne null;
     *              Un livre si il y en a un ayant pour ISBN celui donné.
     */
    Book searchBookFor(final String isbn);
}
