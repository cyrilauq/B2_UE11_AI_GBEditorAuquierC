package org.helmo.gbeditor.infrastructures.exception;

public class CloseRessourcesException extends RuntimeException {

    public CloseRessourcesException(final String message, final Exception ex) {
        super(message, ex);
    }

}
