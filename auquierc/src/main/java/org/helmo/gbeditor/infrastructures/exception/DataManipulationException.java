package org.helmo.gbeditor.infrastructures.exception;

public class DataManipulationException extends RuntimeException {

    /**
     * Crée une nouvelle DataManipulationException avec un message et une exception de départ donnée.
     *
     * @param message   Message de l'exception.
     * @param e         Exception d'origine.
     */
    public DataManipulationException(final String message, final Exception e) {
        super(message, e);
    }

    /**
     * Crée une nouvelle DataManipulationException avec une exception de départ donnée.
     *
     * @param e Exception d'origine.
     */
    public DataManipulationException(final Exception e) {
        super(e);
    }

}
