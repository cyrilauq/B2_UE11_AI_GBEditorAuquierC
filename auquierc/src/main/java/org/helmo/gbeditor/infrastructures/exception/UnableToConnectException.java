package org.helmo.gbeditor.infrastructures.exception;

import org.helmo.gbeditor.repositories.exceptions.DataManipulationException;

/**
 * Définit une exception qui sera lancée lorsque la connexion à la base de donnée aura échoué.
 */
public class UnableToConnectException extends DataManipulationException {

    /**
     * Crée une nouvelle UnableToConnectException avec un message et une exception source donnés.
     *
     * @param message   Message de l'exception.
     * @param ex        Exception source.
     */
    public UnableToConnectException(final String message, final Exception ex) {
        super(message, ex);
    }

}
