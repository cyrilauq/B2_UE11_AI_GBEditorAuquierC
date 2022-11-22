package org.helmo.gbeditor.domains;

/**
 *
 * S'occupe de gérer la session de l'utilisateur connecté.
 * Elle permettra, en outre, de connaitre le livre que l'utilisateur vue modifier ou supprimer.
 *
 * @author  Cyril Auquier
 *
 */
public class Session {
    private Author author = new Author("Unknown", "", "1234567");
    private String currentIsbn;
    private Book currentBook;
    private String currentPageContent;

    /**
     * Attribue à la session courante un auteur.
     *
     * @param name          Nom de l'auteur
     * @param firstname     Prénom de l'auteur
     */
    public void setAuthor(String name, String firstname) {
        this.author = new Author(name, firstname, "e200106");
    }

    /**
     * Retourne l'auteur courant avec le format [name] [firstname]
     *
     * @return L'auteur connecté.
     */
    public String getAuthor() {
        return author.getName() + " " + author.getFirstname();
    }

    /**
     * Retourne le matricule de l'auteur connecté.
     *
     * @return  Matricule de l'auteur connecté.
     */
    public String getMatricule() {
        return author.getMatricule();
    }

    public void setCurrentIsbn(final String isbn) {
        currentIsbn = isbn;
    }

    public String getCurrentPageContent() {
        return currentPageContent;
    }

    public void setCurrentPageContent(String currentPageContent) {
        this.currentPageContent = currentPageContent;
    }

    public String getCurrentIsbn() {
        return currentIsbn;
    }

    public Book getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }
}
