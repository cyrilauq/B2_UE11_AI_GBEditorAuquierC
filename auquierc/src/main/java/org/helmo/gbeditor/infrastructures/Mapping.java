package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.*;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Définit la manière dont on convertit un BookDTO vers en Book et vis-versa, et les méthodes utiles à ces conversions.
 * TODO : ne pas faire la conversion dans de l'ISBN lorsqu'on l'initialise mais lors qu'on converti Book => DTO et vice versa.
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
                        convertISBNFromDTO(dto.getIsbn()),
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
                        convertISBNFromDTO(dto.getIsbn()),
                        dto.getResume(),
                        dto.getAuthor()
                ),
                dto.getImgPath()
        );
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
                        convertISBNFromDTO(dto.getIsbn()),
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
        final var date = b.get(BookFieldName.PUBLISH_DATE);
        return new BookDTO(
                b.get(BookFieldName.TITLE),
                convertISBNToDTO(b.get(BookFieldName.ISBN)),
                b.get(BookFieldName.AUTHOR),
                b.get(BookFieldName.SUMMARY),
                b.getImgPath(),
                BookDTO.CURRENT_VERSION,
                convertPages(b.iterator()),
                date == null ? null : LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm"))
        );
    }

    private static List<Page> convertToPages(final List<PageDTO> pages) {
        final List<Page> result = new ArrayList<>();
        pages.forEach(p -> result.add(new Page(p.getContent())));
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

    /**
     * Convertit un isbn donné en isbn prévu pour l'affichage.
     *
     * @param isbn  Isbn à convertir
     *
     * @return      Convertit l'isbn pour qu'il soit formatté pour l'affichage dans l'application.
     *              Exemple : isbn côté affichage 2-200106-30-11 ==> isbn côté mémoire 2-200106-30-0
     */
    public static String convertISBNFromDTO(final String isbn) {
        final var temp = isbn.replaceAll("-", "");
        if(temp.length() == ISBN.ISBN_MAX_LENGTH) {
            String result = temp.substring(0, 9);
            if(temp.endsWith("10")) {
                return result +  "X";
            }
            return result +  "0";
        }
        return temp;
    }

    /**
     * Convertit un isbn donné en isbn stocké coté mémoire.
     *
     * @param isbn  Isbn à convertir
     *
     * @return      Convertit l'isbn pour qu'il soit formatté pour le côté mémoire de l'application.
     *              Exemple : isbn côté affichage 2-200106-30-0 ==> isbn côté mémoire 2-200106-30-11
     */
    public static String convertISBNToDTO(final String isbn) {
        final var temp = isbn.replaceAll("-", "");
        if(temp.length() == ISBN.ISBN_MIN_LENGTH) {
            String result = temp.substring(0, 9);
            if(temp.endsWith("0")) {
                return result +  "11";
            } else if(temp.endsWith("X")) {
                return result +  "10";
            }
        }
        return temp;
    }

}
