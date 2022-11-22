package org.helmo.gbeditor.domains;

/**
 *
 * Définit le comportement d'un auteur et son initialisation.
 *
 */
public class Author {
    private final String name;
    private final String firstname;
    private final String matricule;

    /**
     * Crée un nouvel auteur
     *
     * @param name          Nom de l'auteur
     * @param firstname     Prénom de l'auteur
     * @param matricule     Matricule de l'auteur
     */
    public Author(final String name, final String firstname, final String matricule) {
        this.name = name;
        this.firstname = firstname;
        this.matricule = matricule;
    }

    public String getMatricule() {
        return matricule;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getName() {
        return name;
    }
}
