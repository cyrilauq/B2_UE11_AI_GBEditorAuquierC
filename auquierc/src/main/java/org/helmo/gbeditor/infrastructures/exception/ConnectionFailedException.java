package org.helmo.gbeditor.infrastructures.exception;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(final String message, final Exception ex) {
        super(message, ex);
    }
}
