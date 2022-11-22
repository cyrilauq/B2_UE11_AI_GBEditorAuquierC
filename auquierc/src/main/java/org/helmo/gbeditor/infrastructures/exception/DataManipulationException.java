package org.helmo.gbeditor.infrastructures.exception;

public class DataManipulationException extends RuntimeException {

    public DataManipulationException(final String message, final Exception ex) {
        super(message, ex);
    }

}
