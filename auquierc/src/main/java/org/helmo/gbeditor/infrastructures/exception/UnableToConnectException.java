package org.helmo.gbeditor.infrastructures.exception;

public class UnableToConnectException extends RuntimeException {

    public UnableToConnectException(final String message, final Exception ex) {
        super(message, ex);
    }

}
