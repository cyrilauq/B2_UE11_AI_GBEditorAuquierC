package org.helmo.gbeditor.infrastructures.exception;

public class UnableToOpenResourceException extends RuntimeException {

    public UnableToOpenResourceException(String message, Exception exception) {
        super(message, exception);
    }

}
