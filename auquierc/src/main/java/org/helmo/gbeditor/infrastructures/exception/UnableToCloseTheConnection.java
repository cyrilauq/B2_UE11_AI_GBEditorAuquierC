package org.helmo.gbeditor.infrastructures.exception;

public class UnableToCloseTheConnection extends RuntimeException {
    public UnableToCloseTheConnection(Exception e) {
        super(e);
    }
}
