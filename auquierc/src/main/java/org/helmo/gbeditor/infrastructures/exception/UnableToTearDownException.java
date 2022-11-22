package org.helmo.gbeditor.infrastructures.exception;

public class UnableToTearDownException extends RuntimeException {

    public UnableToTearDownException(final String message, final Exception ex) {
        super(message, ex);
    }

}
