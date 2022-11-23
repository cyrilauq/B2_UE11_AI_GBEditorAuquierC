package org.helmo.gbeditor.infrastructures.exception;

/**
 * Définit une erreur qui sera lancée lorsqu'un livre qu'on veut ajouter existe déjà.
 */
public class BookAlreadyExistsException extends RuntimeException {

    /**
     * Crée une nouvelle erreur de type BookAlreadyExistsException
     *
     * @param msg Message à donner à l'erreur.
     */
    public BookAlreadyExistsException(final String msg) {
        super(msg);
    }
}
