package org.helmo.gbeditor.repositories.exceptions;

/**
 * Cette classe définit une exception qui sera lancée lorsqu'un problème surviendra lors de la sauvegarde d'une page d'une livre.
 */
public class UnableToSavePageException extends RuntimeException {

    /**
     * Crée une nouvelle UnableToSavePageException avec un message et une exception source donnés.
     *
     * @param message   Message de l'exception.
     * @param e         Exception source.
     */
    public UnableToSavePageException(final String message, final Exception e) {
        super(message, e);
    }

}
