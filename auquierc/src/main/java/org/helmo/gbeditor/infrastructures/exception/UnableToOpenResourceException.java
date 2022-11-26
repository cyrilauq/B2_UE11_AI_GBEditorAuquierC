package org.helmo.gbeditor.infrastructures.exception;

/**
 * Définit une exception qui sera lancée quand l'application ne sera pas capable d'ouvrir la ressources utilisée.
 */
public class UnableToOpenResourceException extends RuntimeException {

    /**
     * Crée une nouvelle UnableToOpenResourceException avec un message et une exception d'origine donnés.
     *
     * @param message   Message de l'exception.
     * @param exception Exception d'origine.
     */
    public UnableToOpenResourceException(String message, Exception exception) {
        super(message, exception);
    }

}
