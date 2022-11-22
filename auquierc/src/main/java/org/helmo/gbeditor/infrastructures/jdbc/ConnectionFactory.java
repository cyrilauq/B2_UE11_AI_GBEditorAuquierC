package org.helmo.gbeditor.infrastructures.jdbc;

import org.helmo.gbeditor.infrastructures.exception.UnableToConnectException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Cette classe permet d'initaliser une nouvelle ConnectionFactory qui sauvegardera l'url de la base de données, le login et le mot de passe pour s'y connecté.
 */
public class ConnectionFactory {
    private final String bdUrl;
    private final String user;
    private final String password;

    /**
     * Crée une nouvelle ConnectionFactory à partir d'une url de base de données, d'un login et d'un mot de passe donné.
     *
     * @param bdUrl     Url de la base de données.
     * @param user      Login de la base de données.
     * @param password  Mot de passe lié au login pour se connecter à la base de données.
     */
    public ConnectionFactory(final String bdUrl, final String user, final String password) {
        this.bdUrl = bdUrl;
        this.user = user;
        this.password = password;
    }

    /**
     * Créer une nouvelle connection à partir des informations sauvegardées par la ConnectionFactory.
     *
     * @return  Une nouvelle connection à la base de données mémorisée par la factory.
     *
     * @throws UnableToConnectException Si la connection n'a pas pu se faire.
     */
    public Connection newConnection() throws UnableToConnectException {
        try {
            return DriverManager.getConnection(bdUrl, user, password);
        } catch (SQLException e) {
            throw new UnableToConnectException("La connection avec la ressource n'a pas pu se faire.", e);
        }
    }

}
