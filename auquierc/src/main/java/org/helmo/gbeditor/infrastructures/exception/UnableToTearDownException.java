package org.helmo.gbeditor.infrastructures.exception;

/**
 * Définit une exception qui sera lancée lorsque le tearDown des tests de BDRepository ne saura pas se faire.
 */
public class UnableToTearDownException extends RuntimeException {

    /**
     * Crée une nouvelle UnableToTearDownException avec un message et une erreur d'origine donnés.
     *
     * @param message   Message de l'exception
     * @param ex        Exception source.
     */
    public UnableToTearDownException(final String message, final Exception ex) {
        super(message, ex);
    }

}
