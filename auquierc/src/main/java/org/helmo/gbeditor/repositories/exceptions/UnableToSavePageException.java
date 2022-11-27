package org.helmo.gbeditor.repositories.exceptions;

public class UnableToSavePageException extends RuntimeException {

    public UnableToSavePageException(final String message, final Exception e) {
        super(message, e);
    }

}
