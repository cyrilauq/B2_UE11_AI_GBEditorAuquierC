package org.helmo.gbeditor.infrastructures.exception;

public class DataManipulationException extends RuntimeException {

    public DataManipulationException(final String message, final Exception e) {
        super(message, e);
    }

    public DataManipulationException(final Exception e) {
        super(e);
    }

}
