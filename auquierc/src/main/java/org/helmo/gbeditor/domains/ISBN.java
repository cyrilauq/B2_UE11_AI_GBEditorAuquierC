package org.helmo.gbeditor.domains;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Cette classe s'occupe de toutes les opérations liées à un ISBN.
 *
 * @author  Cyril Auquier
 */
public class ISBN {
    private final String codeIsbn;

    /**
     * Crée un nouvel ISBN.
     *
     * @param isbn  Chaîne de caractères représentant le code ISBN
     */
    public ISBN(String isbn) {
        String temp = isbn.replaceAll("-", "");
        if(!temp.endsWith("10") && (temp.endsWith("0") || temp.endsWith("X"))) {
            String result = temp.substring(0, 9);
            if(temp.endsWith("0")) {
                this.codeIsbn = result +  "11";
            } else {
                this.codeIsbn = result +  "10";
            }
        } else {
            this.codeIsbn = temp;
        }
    }

    /**
     * Créer un nouvel objet ISBN à partir d'une chaine de caractères donnée représentant un code ISBN.
     *
     * @param isbn  Code de l'ISBN à créer.
     *
     * @return      Un nouvel Objet ISBN avec le code donné.
     */
    public static ISBN of(final String isbn) {
        if(isbn == null) {
            throw new WrongFormattedISBNException("The ISBN cannot be null");
        }
        return new ISBN(isbn);
    }

    /**
     * Vérifié qu'un code ISBN donné est valide.
     * Si le code ISBN est valide, la méthode renverra null, sinon elle renverra un message désignant ce qui ne va pas avec l'ISBN donné.
     *
     * @param isbn          Code ISBN
     * @param matriculeId   Matricule de l'auteur
     *
     * @return  Null si tout est bon
     *          Si le code ISBN n'est pas valide, renvoie une chaine de caractères spécifiant ce qui ne va pas.
     */
    public static String isValid(String isbn, final String matriculeId) {
        if(isbn == null || isbn.isBlank()) { return ISBNTypeError.EMTY_ISBN.message; }
        final String isbnReplaced = isbn.replaceAll("-", "");
        String validFormat = validFormatFor(isbnReplaced);
        if(validFormat == null) {
            return validDataFor(isbnReplaced, matriculeId);
        }
        return validFormat;
    }

    /**
     * Valide les données contenue dans un ISBN donné.
     *
     * @param isbn          Code ISBN, du livre, que l'on cherche à valider
     * @param matriculeId   Matricule de l'auteur, du livre.
     *
     * @return      Si le code ISBN n'a pas le code linguistique 2, retourne un message d'erreur
     *              Si l'ISBN donnée ne contient pas le matricule donné, retourne un message d'erreur
     *              Si le code de vérification de l'ISBN n'est pas valide, retourne un message d'erreur
     *              Sinon, on considère que les données contenue dans l'ISBN sont valides, la méthode
     *              retourne alors null.
     */
    private static String validDataFor(final String isbn, final String matriculeId) {
        if(isbn.charAt(0) != '2') {
            return ISBNTypeError.WRONG_LANG_CODE.message;
        } else if(!Pattern.matches("[0-9]" + matriculeId + "([0-9]+)[A-Za-z]?", isbn)) {
            return ISBNTypeError.WRONG_AUTHOR.message;
        } else if(getControlNumber(isbn) != controlNumberFor(isbn)) {
            return ISBNTypeError.WRONG_CONTROL_NUMBER.message;
        }
        return null;
    }

    private static int getControlNumber(final String isbn) {
        switch (isbn.substring(9)) {
            case "X":
            case "x":
                return 10;
            case "0":
                return 11;
            default:
                return Integer.parseInt(isbn.substring(9));
        }
    }

    /**
     * Vérifie que le format de l'ISBN donné est valide.
     *
     * @param isbn  Code ISBN, du livre, que l'on cherche à valider
     *
     * @return      Si l'ISBN donné est null ou vide, retourne un message d'erreur.
     *              Si l'ISBN contient des lettres autres que X, retourne un message d'erreur.
     *              Si la longueur de l'ISBN et différente de 10 et de 11, retourne un message d'erreur.
     *              Sinon, on considère que les données contenue dans l'ISBN sont valides, la méthode
     *              retourne alors null.
     */
    private static String validFormatFor(final String isbn) {
        if(containsLetter(isbn)) {
            return ISBNTypeError.CONTAINS_LETTER.message;
        } else if(isbn.length() != 10 && isbn.length() != 11) {
            return ISBNTypeError.TOO_MANY_CHARACTER.message;
        }
        return null;
    }

    /**
     * Vérifie qu'un ISBN donné ne contient que des chiffres avec ou sans la lettre X.
     *
     * @param isbn  Code ISBN, du livre, que l'on cherche à valider
     *
     * @return      False, si l'ISBN donné contient autre chose que des chiffres ou la lettre X
     *              True, sinon.
     */
    private static boolean containsLetter(final String isbn) {
        return Pattern.compile("[^0-9|Xx]+").matcher(isbn).find();
    }

    /**
     * <p>Calcul le code de vérification pour un ISBN donné.</p>
     * <p>
     * Il se calcule de la manière suivante :
     * <ul>
     * <li>2 200106 30</li>
     * <li>2 * 10 => 20</li>
     * <li>2 * 9 => 18</li>
     * <li>0 * 8 => 0</li>
     * <li>0 * 7 => 0</li>
     * <li>1 * 6 => 6</li>
     * <li>0 * 5 => 0</li>
     * <li>6 * 4 => 24</li>
     * <li>3 * 3 => 9</li>
     * <li>0 * 2 => 0</li>
     * </ul>
     * ===> 77 % 11 = 0 => 11 - 0 = 11
     *</p>
     *
     * @param isbn  Code ISBN dont on veut connaitre le code de vérification.
     *
     * @return      Retourne le code de vérification correspondant à l'ISBN donné.
     */
    private static int controlNumberFor(final String isbn) {
        int total = 0;
        int i;
        for(i = 0; i < 10 - 1; i++) {
            total += Integer.parseInt(isbn.charAt(i) + "") * (10 - i);
        }
        return 11 - (total % 11);
    }

    /**
     * Génère l'affiche de l'ISBN pour l'utilisateur.
     *
     * @return  Le format d'affichage à afficher à l'utilisateur comme suit :
     *          <ul>
     *          <li>Si l'ISBN courant contient 10 alors il sera remplacé par X.</li>
     *          <li>Si l'ISBN courant contient 11 alors il sera remplacé par 0.</li>
     *          <li>Sinon l'ISBN est affiché sans modification côté utilisateur</li>
     *          </ul>
     */
    public String forUser() {
        String result = codeIsbn;
        if(result.length() == 11) {
            if(result.endsWith("10")) {
                result = codeIsbn.substring(0, 9) + "X";
            } else {
                result = codeIsbn.substring(0, 9) + "0";
            }
        }
        return formatIsbn(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ISBN isbn = (ISBN) o;
        return Objects.equals(codeIsbn, isbn.codeIsbn);
    }

    @Override
    public String toString() {
        return formatIsbn(codeIsbn);
    }

    private String formatIsbn(final String codeIsbn) {
        if(codeIsbn.length() < 10) { return codeIsbn; }
        var builder = new StringBuilder(codeIsbn)
                .insert(9, "-")
                .insert(7, "-")
                .insert(1, "-");
        return builder.toString();
    }

    /**
     * Cette énumération définit les différents types d'erreurs qui peuvent être detectées lors de la création d'un ISBN.
     */
    public enum ISBNTypeError {
        WRONG_LANG_CODE("Le groupe linguistique n'est pas valide"),
        WRONG_AUTHOR("Le matricule de l'auteur ne correspond pas à celui de l'auteur connecté."),
        WRONG_CONTROL_NUMBER("Le numéro de contrôle n'est pas valide."),
        EMTY_ISBN("L'ISBN ne peut pas être vide"),
        TOO_MANY_CHARACTER("L'ISBN doit avoir une longueur de 10 chiffres."),
        CONTAINS_LETTER("L'ISBN ne peut contenir que des nombre.");

        private final String message;

        ISBNTypeError(final String message) { this.message = message; }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Définit une nouvelle exception qui représente une erreur de formattage d'ISBN.
     */
    public static class WrongFormattedISBNException extends RuntimeException {

        /**
         * Crée une nouvelle exception WrongFormattedISBNException sur base d'un message donné.
         *
         * @param message   Message de l'erreur.
         */
        public WrongFormattedISBNException(final String message) { super(message); }
    }
}
