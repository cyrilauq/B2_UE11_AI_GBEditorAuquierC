package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.BookFieldName;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;

import java.util.HashMap;
import java.util.Map;

public class Tracker {

    private final Map<Book, BookDTO> tracker = new HashMap<>();

    /**
     * Ajoute un livre et le dto lui correspondant au tracker.
     *
     * @param book  Livre
     * @param dto   DTO du livre
     */
    public void put(final Book book, final BookDTO dto) {
        tracker.put(book, dto);
    }

    /**
     * Vide le tracker.
     */
    public void clear() {
        tracker.clear();
    }

    /**
     * Détermine si le tracker contient un livre donné.
     *
     * @param book  Livre recherché
     *
     * @return      True si le livre se trouve dans le tracker
     *              False si le livre ne s'y trouve pas.
     */
    public boolean contains(final Book book) {
        return tracker.get(book).getIsbn().equals(book.get(BookFieldName.SYS_ISBN));
    }

    /**
     * Retourne l'identifiant en base de donnée du DTO lié au livre donné.
     *
     * @param book  Livre dont on cherche l'identifiant.
     *
     * @return      L'identifiant en base de donnée du livre recherché.
     */
    public int getIdBookFor(final Book book) {
        var result = -1;
        for (final var entry: tracker.entrySet()) {
            if(entry.getKey() == book || entry.getKey().equals(book)) {
                result = entry.getValue().id;
            }
        }
        return result;
    }

}
