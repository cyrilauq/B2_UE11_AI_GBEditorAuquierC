package org.helmo.gbeditor.domains.factory;

import org.helmo.gbeditor.domains.*;

/**
 * Cette classe définit des méthodes permettant de créer un livre cohérent et une méthode permettant de définir
 * si les informations d'un livre sont valides ou non.
 */
public class BookFactory {
    private final static int TITLE_MAX_LENGTH = 150;
    private final static int SUMMARY_MAX_LENGTH = 500;

    /**
     * Crée un livre sur base des informations données et vérifie que ces données soient correctes et cohérentes.
     * Elle doivent être non null et non vide.
     *
     * @param metadata          Les données générales du livre.
     * @param authorMatricule   Le matricule de l'auteur du livre.
     * @param filePath          Le chemin d'accès à l'image de couverture du livre.
     *
     * @return                  Un livre correctement formatté.
     *                          C'est à dire avec:
     *                              - un titre non null et non vide
     *                              - un auteur non null et non vide
     *                              - un isbn non null et non vide
     *                              - un résumé non null et non vide
     *                          Si les infos données ne sont pas valide la méthode renvoie une WrongFormattedBookException.
     */
    public static Book of(final BookMetadata metadata, String authorMatricule, final String filePath) {
        var message = validBook(metadata.get(BookFieldName.TITLE),
                metadata.get(BookFieldName.SUMMARY), ISBN.isValid(metadata.get(BookFieldName.ISBN), authorMatricule));
        if(message != null) {
            throw new Book.WrongFormattedBookException(message);
        }
        return new Book(
                metadata,
                filePath
        );
    }

    /**
     * Crée un livre sur base des informations données et vérifie que ces données soient correctes et cohérentes.
     * Elles doivent être non null et non vide.
     *
     * @param metadata          Les données générales du livre.
     * @param authorMatricule   Le matricule de l'auteur du livre.
     *
     * @return                  Un livre correctement formatté.
     *                          C'est-à-dire avec :
     *                              - un titre non null et non vide
     *                              - un auteur non null et non vide
     *                              - un isbn non null et non vide
     *                              - un résumé non null et non vide
     *                          Si les infos données ne sont pas valide la méthode renvoie une WrongFormattedBookException.
     */
    public static Book of(final BookMetadata metadata, String authorMatricule) {
        var message = validBook(metadata.get(BookFieldName.TITLE),
                metadata.get(BookFieldName.SUMMARY), ISBN.isValid(metadata.get(BookFieldName.ISBN), authorMatricule));
        if(message != null) {
            throw new Book.WrongFormattedBookException(message);
        }
        return new Book(
                metadata
        );
    }

    /**
     * Vérifie si les informations d'un livre sont correctes ou non.
     *
     * @param title     Titre du livre
     * @param resume    Résumé du livre
     * @param validIsbn Message de vérification obtenu après avoir vérifier l'isbn
     *
     * @return          Null si les informations sont valides.
     *                  Une string non vide si une des informations et null, vide ou dépasse une longueur maximale.
     */
    public static String validBook(String title, String resume, String validIsbn) {
        if(title == null || title.isBlank()) {
            return BookTypeError.EMPTY_TITLE.getMessage();
        } else if (resume == null || resume.isBlank()) {
            return BookTypeError.EMPTY_RESUME.getMessage();
        }
        return verifyContent(title, resume, validIsbn);
    }

    private static String verifyContent(final String title, final String resume, final String validIsbn) {
        if(title.length() > TITLE_MAX_LENGTH) {
            return BookTypeError.TITLE_TOO_LONG.getMessage();
        } else if(resume.length() > SUMMARY_MAX_LENGTH) {
            return BookTypeError.RESUME_TOO_LONG.getMessage();
        }
        return validIsbn;
    }
}
