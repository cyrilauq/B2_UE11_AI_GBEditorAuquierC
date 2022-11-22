package org.helmo.gbeditor.infrastructures.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Cette classe s'occupe de gérer le manière dont un livre sera stocké.
 */
public class BookDTO implements Comparable<BookDTO>, Iterable<PageDTO> {
    @SerializedName("title")
    public final String title;
    @SerializedName("isbn")
    public final String isbn;
    @SerializedName("author")
    public final String author;
    @SerializedName("resume")
    public final String resume;
    @SerializedName("imgPath")
    public final String imgPath;
    @SerializedName("pages")
    public final List<PageDTO> pages = new ArrayList<>();

    @SerializedName("version")
    public String version = "1";
    @SerializedName("publishDate")
    public String publishDate;

    @Expose(serialize = false, deserialize = false)
    public int id = 0;

    public static final String CURRENT_VERSION = "1.2";

    /**
     * Crée un nouveau BookDTO avec un titre, un code ISBN, un auteur et un résumé donné.
     *
     * @param title     Titre du BookDTO
     * @param isbn      Code ISBN du BookDTO
     * @param author    Auteur du BookDTO
     * @param resume    Résumé du BookDTO
     */
    public BookDTO(final String title, final String isbn, final String author, final String resume, final String imgPath, final String version) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.resume = resume;
        this.imgPath = imgPath;
        this.version = version;
    }

    public List<PageDTO> getPages() {
        return pages;
    }

    /**
     * Crée un nouveau BookDTO avec un titre, un code ISBN, un auteur et un résumé donné.
     *
     * @param title     Titre du BookDTO
     * @param isbn      Code ISBN du BookDTO
     * @param author    Auteur du BookDTO
     * @param resume    Résumé du BookDTO
     * @param pages     Pages du BookDTO
     * @param version   Version du BookDTO
     */
    public BookDTO(final String title, final String isbn, final String author, final String resume, final String imgPath, final String version, final List<PageDTO> pages) {
        this(title, isbn, author, resume, imgPath, version);
        this.pages.addAll(pages);
    }

    /**
     * Crée un nouveau BookDTO avec un titre, un code ISBN, un auteur et un résumé donné.
     *
     * @param title     Titre du BookDTO
     * @param isbn      Code ISBN du BookDTO
     * @param author    Auteur du BookDTO
     * @param resume    Résumé du BookDTO
     * @param pages     Pages du BookDTO
     * @param publishDate     Date de publication du livre
     * @param version   Version du BookDTO
     */
    public BookDTO(final String title, final String isbn, final String author, final String resume, final String imgPath, final String version, final List<PageDTO> pages, final LocalDateTime publishDate) {
        this(title, isbn, author, resume, imgPath, version);
        this.pages.addAll(pages);
        this.publishDate = publishDate == null ? null : publishDate.format(DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm"));
    }

    public String getTitle() {
        return title;
    }
    public LocalDateTime getPublishDate() {
        if(publishDate == null) {
            return null;
        }
        return LocalDateTime.parse(publishDate, DateTimeFormatter.ofPattern("dd-MM-yy à HH:mm"));
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() { return isbn.replaceAll("-", ""); }

    public String getResume() {
        return resume;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getVersion() {
        return version == null ? "1" : version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BookDTO bookDTO = (BookDTO) o;
        return Objects.equals(title, bookDTO.title) && Objects.equals(isbn, bookDTO.isbn) && Objects.equals(author, bookDTO.author) && Objects.equals(resume, bookDTO.resume);
    }

    @Override
    public int compareTo(BookDTO o) {
        if(this.isbn == null || this.isbn.length() < 10) {
            return -1;
        }
        return this.isbn.substring(7, 8).compareTo(o.isbn.substring(7, 8));
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", author='" + author + '\'' +
                ", resume='" + resume + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", pages=" + pages +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public Iterator<PageDTO> iterator() {
        if(pages == null) {
            return Collections.emptyIterator();
        }
        return pages.iterator();
    }
}
