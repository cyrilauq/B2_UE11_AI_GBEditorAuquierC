package org.helmo.gbeditor.factory;

import org.helmo.gbeditor.domains.ISBN;

/**
 * Cette classe s'occupe de créer des ISBN correctement formaté.
 */
public class ISBNFactory {

    /**
     * Génère automatiquement le code ISBN pour un nouveau livre.
     *
     * @param langCode      Code du groupe linguistique auquel appartient le livre
     * @param authorMat     Matricule de l'auteur ayant encodé le livre.
     * @param nBook         Le numéro du livre courant.
     *
     * @return              Un isbn valide résultant d'un calcul réalisé avec les informations données.
     */
    public static ISBN computeISBNFor(int langCode, String authorMat, int nBook) {
        final String isbn = langCode + authorMat + (nBook < 10 ? "0" : "") + nBook;
        int total = 0;
        for(int i = 0; i < 9; i++) {
            total += Integer.parseInt(isbn.charAt(i) + "") * (10 - i);
        }
        final int temp = 11 - (total % 11);
        String result = isbn + temp;
        if(result.length() == ISBN.ISBN_MAX_LENGTH) {
            String result2 = isbn.substring(0, 9);
            if(result.endsWith("10")) {
                result = result2 +  "X";
            } else {
                result = result2 +  "0";
            }
        }
        return new ISBN(formatIsbn(result));
    }

    /**
     * Format l'isbn pour qu'il soit représenté comme ici : 1-234567-89-0
     *
     * @param codeIsbn  Isbn à formatter.
     *
     * @return          L'isbn donné formatté comme suit : 1-234567-89-0
     */
    public static String formatIsbn(final String codeIsbn) {
        if(codeIsbn.length() < ISBN.ISBN_MIN_LENGTH) { return codeIsbn; }
        var builder = new StringBuilder(codeIsbn)
                .insert(9, "-")
                .insert(7, "-")
                .insert(1, "-");
        return builder.toString();
    }
}
