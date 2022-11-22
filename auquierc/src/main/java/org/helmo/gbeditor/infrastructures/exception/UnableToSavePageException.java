package org.helmo.gbeditor.infrastructures.exception;

public class UnableToSavePageException extends RuntimeException {

    public UnableToSavePageException(final String message, final Exception e) {
        super(message, e);
    }

}
