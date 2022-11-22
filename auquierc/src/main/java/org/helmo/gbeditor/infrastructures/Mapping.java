package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.BookMetadata;
import org.helmo.gbeditor.domains.Page;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;

import java.util.*;

/**
 *
 * Définit la manière dont on convertit un BookDTO vers en Book et vis-versa, et les méthodes utiles à ces conversions.
 *
 */
public class Mapping {

    /**
     * Convertit un BookDTO en Book
     *
     * @param dto   BookDTO qu'on veut convertir
     *
     * @return      L'équivalent Book du BookDTO donnée.
     */
    public static Book convertToBook(BookDTO dto) {
        switch (dto.getVersion()) {
            case "1.1":
                return FromV1_1(dto);
            case "1.2":
                return FromV1_2(dto);
            default:
                return FromV1(dto);
        }
    }

    /**
     * Convertit un BookDTO de version 1 vers un objet Book.
     *
     * @return  Un objet Book représentant l'équivalent du BookDTO courant.
     */
    private static Book FromV1(BookDTO dto) {
        return new Book(
                new BookMetadata(
                        dto.getTitle(),
                        dto.getIsbn(),
                        dto.getResume(),
                        dto.getAuthor()
                )
        );
    }

    /**
     * Convertit un BookDTO de version 1.1 vers un objet Book.
     *
     * @return  Un objet Book représentant l'équivalent du BookDTO courant.
     */
    private static Book FromV1_1(BookDTO dto) {
        return new Book(
                new BookMetadata(
                        dto.getTitle(),
                        dto.getIsbn(),
                        dto.getResume(),
                        dto.getAuthor()
                ),
                dto.getImgPath()
        );
    }

    /**
     * Convertit une dto en petit Book.
     * Seules les informations servant à la présentation seront utilisées.
     *
     * @param dto   DTO à convertir
     *
     * @return
     */
    public static Book smallConvertToBook(final BookDTO dto) {
        // TODO : implementé smallConvertToBook
        return null;
    }

    /**
     * Convertit toutes les informations d'un DTO en livre.
     *
     * @param dto   DTO à convertir
     *
     * @return
     */
    public static Book bigConvertToBook(final BookDTO dto) {
        // TODO : implementé smallConvertToBook
        return null;
    }

    /**
     * Convertit un BookDTO de version 1.1 vers un objet Book.
     *
     * @return  Un objet Book représentant l'équivalent du BookDTO courant.
     */
    private static Book FromV1_2(BookDTO dto) {
        final var result = new Book(
                new BookMetadata(
                        dto.getTitle(),
                        dto.getIsbn(),
                        dto.getResume(),
                        dto.getAuthor()
                ),
                dto.getImgPath()
        );
        result.setPublishDate(dto.getPublishDate());
        convertToPages(dto.getPages()).forEach(
                result::addEnd
        );
        return result;
    }

    /**
     * Convertit un Book en BookDTO
     *
     * @param b     Book qu'on veut convertir
     *
     * @return      L'équivalent BookDTO du Book donnée.
     */
    public static BookDTO convertToBookDTO(Book b) {
        return new BookDTO(
                b.getTitle(),
                b.getIsbn(),
                b.getAuthor(),
                b.getResume(),
                b.getImgPath(),
                BookDTO.CURRENT_VERSION,
                convertPages(b.iterator()),
                b.getPublishDate()
        );
    }

    private static List<Page> convertToPages(final List<PageDTO> pages) {
        final List<Page> result = new ArrayList<>();
        pages.forEach(p -> {
            result.add(new Page(p.getContent()));
        });
        for(int i = 0; i < result.size(); i++) {
            var page = result.get(i);
            var choices = pages.get(i).getChoices();
            choices.forEach((c, v) -> {
                if(v.matches("[0-9]+")) {
                    page.addChoice(c, result.get(Integer.parseInt(v) - 1));
                } else {
                    page.addChoice(c, getPageFor(v, result));
                }
            });
        }
        return result;
    }

    private static Page getPageFor(final String pageContent, final List<Page> pages) {
        for(final var p : pages) {
            if(p.getContent().equalsIgnoreCase(pageContent)) {
                return p;
            }
        }
        return null;
    }

    private static Map<String, String> convertChoices(final Page page) {
        final Map<String, String> result = new TreeMap<>();
        page.forEach(p -> result.put(p, page.getPageForChoice(p).getContent()));
        return result;
    }

    private static List<PageDTO> convertPages(final Iterator<Page> choices) {
        final List<PageDTO> result = new ArrayList<>();
        for(int i = 0; choices.hasNext(); i++) {
            final var page = choices.next();
            result.add(new PageDTO(
                    page.getContent(),
                    convertChoices(page),
                    i+1
            ));
        }
        return result;
    }

}
