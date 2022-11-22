package org.helmo.gbeditor.factory;

import org.helmo.gbeditor.domains.ISBN;

/**
 * Cette classe s'occupe de créer des ISBN correctement formaté.
 */
public class ISBNFactory {

    /**
     * Génére automatiquement le code ISBN pour un nouveau livre.
     *
     * @param langCode      Code du groupe linguistique auquel appartient le livre
     * @param authorMat     Matricule de l'auteur ayant encodé le livre.
     * @param nBook       Le numéro du livre courant.
     *
     * @return
     */
    public static ISBN computeISBNFor(int langCode, String authorMat, int nBook) {
        final String isbn = langCode + authorMat + (nBook < 10 ? "0" : "") + nBook;
        int total = 0;
        for(int i = 0; i < 9; i++) {
            total += Integer.parseInt(isbn.charAt(i) + "") * (10 - i);
        }
        final int temp = 11 - (total % 11);
        return new ISBN(isbn + temp);
    }
}
