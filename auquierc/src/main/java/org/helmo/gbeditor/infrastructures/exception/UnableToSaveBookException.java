package org.helmo.gbeditor.infrastructures.exception;

public class UnableToSaveBookException extends RuntimeException {

    public UnableToSaveBookException(final String message) {
        super(message);
    }

    public UnableToSaveBookException(final String message, Exception e) {
        super(message, e);
    }

}
