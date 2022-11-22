package org.helmo.gbeditor.domains;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe s'occupe de l'analyse d'un livre donné.
 * Ex:
 *  - connaitre les pages terminales(de fin);
 *  - connaitre les pages qui n'ont aucun lien avec d'autres pages.
 */
public class BookAnalysis {

    public static List<Page> computeTerminatedPages(final Book book) {

        return new ArrayList<>();
    }

    /**
     * Récupère toutes les pages qui n'ont aucun lien avec d'autres pages d'un livre donné.
     * Pour déterminer ça, on va vérifier qu'une page à une page suivante/précédente.
     * Si elle n'en a pas, on vérifie qu'au moins un choix d'une autre page pointe sur la page.
     *
     * @param book  Livre à analyser
     *
     * @return      Les pages du livre donné qui n'ont aucuns liens avec d'autres pages de ce même livre.
     */
    public static List<Page> computeLonelyPages(final Book book) {
        // TODO : Parcourir les pages
        // TODO : Vérifier que la pages courante à une page suivante et/ou une page précédente
        // TODO : Si la page n'a ni page précédente/suivante, vérifier qu'elle apparaisse dans au moins un choix d'un page
        return new ArrayList<>();
    }

}
