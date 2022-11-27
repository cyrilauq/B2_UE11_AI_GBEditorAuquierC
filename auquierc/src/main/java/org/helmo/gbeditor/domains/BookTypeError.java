package org.helmo.gbeditor.domains;

/**
 * Enumère les différents types d'erreurs possible lors de la création d'un livre.
 * Elle permettra, entre autre, de ne pas avoir à modifier les tests si les messages doivent changer.
 *
 * @author cyril
 */
public enum BookTypeError {
    TITLE_TOO_LONG("La longueur du titre ne peut pas être supérieur à 150 caractères."),
    RESUME_TOO_LONG("La longueur du résumé ne peut pas être supérieur à 500 caractères."),
    EMPTY_RESUME("Le champ du résumé ne peut pas être vide."),
    EMPTY_TITLE("Le champ du titre ne peut pas être vide.");

    private final String message;

    BookTypeError(String message) { this.message = message; }

    public String getMessage() {
        return message;
    }
}
