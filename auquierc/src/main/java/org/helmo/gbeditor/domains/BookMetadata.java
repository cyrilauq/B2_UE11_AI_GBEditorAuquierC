package org.helmo.gbeditor.domains;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe permet de stocker les informations générales d'un livre: titre, auteur, isbn et résumé.
 *
 * @author Cyril
 *
 * Pour stocker les attributs d'une BookMetaData j'ai choisi une Map, car je voulais lier des clés(BookFieldName = nom des attributs)
 * à des valeurs(String = valeur des attributs).
 * Pour l'implémentation de la Map j'ai choisi une HashMap, car je ne souhaitais pas avoir de tris sur mes attributs et que j'allais souvent utiliser
 * la méthode get qui a une CTT en O(1) pour une HashMap contrairement à la TreeMap où sa CTT en en O(logN).
 */
public class BookMetadata {
    private final Map<BookFieldName, String> attributes = new HashMap<>();

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
        attributes.put(BookFieldName.AUTHOR, author);
        attributes.put(BookFieldName.SUMMARY, resume);
        attributes.put(BookFieldName.TITLE, title);
        attributes.put(BookFieldName.PUBLISH_DATE, null);
    }

    /**
     * Crée un nouvel objet BookMetaData sur base d'infos données.
     *
     * @param title         Titre du livre
     * @param isbn          ISBN du livre
     * @param resume        Résumé du livre
     * @param author        Auteur du livre
     * @param publishDate   Date de publication du livre.
     */
    public BookMetadata(final String title, final String isbn, final String resume, final String author, final String publishDate) {
        attributes.put(BookFieldName.ISBN, ISBN.of(isbn).forUser());
        attributes.put(BookFieldName.AUTHOR, author);
        attributes.put(BookFieldName.SUMMARY, resume);
        attributes.put(BookFieldName.TITLE, title);
        attributes.put(BookFieldName.PUBLISH_DATE, publishDate);
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
        verifyIfFieldExists(attribute);
        return attributes.get(attribute);
    }

    /**
     * Attribue une valeur donnée à un attribut de la classe donné.
     *
     * @param attribute Attribut auquel on souhaite attribuer une nouvelle valeur.
     * @param value     Nouvelle valeur de l'attribut donné.
     */
    public void set(final BookFieldName attribute, final String value) {
        verifyIfNonImmuableField(attribute);
        verifyIfFieldExists(attribute);
        attributes.put(attribute, value);
    }

    private void verifyIfNonImmuableField(BookFieldName attribute) {
        if(attribute == BookFieldName.AUTHOR || (attribute == BookFieldName.PUBLISH_DATE && get(BookFieldName.PUBLISH_DATE) != null)) {
            throw new IllegalArgumentException("The mentioned field cannot be changed.");
        }
    }

    private void verifyIfFieldExists(BookFieldName attribute) {
        if(attribute == null || !attributes.containsKey(attribute)) {
            throw new FieldNotFoundException("Le champ chercher n'a pas été trouvé.");
        }
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
