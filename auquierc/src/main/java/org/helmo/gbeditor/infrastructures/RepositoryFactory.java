package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.exception.UnableToConnectException;
import org.helmo.gbeditor.infrastructures.jdbc.ConnectionFactory;
import org.helmo.gbeditor.repositories.DataRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.helmo.gbeditor.infrastructures.jdbc.DBConfig.*;

public class RepositoryFactory {

    private boolean isJson = false;
    private String currentAuthor;

    /**
     * Créer une nouvelle RepositoryFactory en spécifiant si la ressources utilisée est le fichier Json ou non.
     * Si ce n'est pas le fichier Json alors on considère que c'est la base de donnée.
     *
     * @param isJson    True si la ressources utilisée est le Json
     *                  False sinon.
     */
    public RepositoryFactory(final boolean isJson) {
        this.isJson = isJson;
    }

    public RepositoryFactory(final RepositoryType repoType) { }

    /**
     * Définit l'auteur actuel, celui actuellement connecté dans l'application
     *
     * @param currentAuthor Nom de l'auteur.
     */
    public void setCurrentAuthor(String currentAuthor) {
        this.currentAuthor = currentAuthor;
    }

    public DataRepository newRepository() {
        if(isJson) {
            return of(Paths.get(System.getProperty("user.home"), "ue36"), "e200106");
        }
        return of(DRIVER, LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASSWORD, currentAuthor);
    }

    /**
     * Crée un nouveau DataRepository sur base du chemin d'accès d'un dossier et du nom d'un fichier.
     *
     * @param filePath  Chemin d'accès du dossier
     * @param fileName  Nom du fichier (sans le ".json")
     *
     * @return          Un DataRepository qui travaille avec le Json.
     */
    public static DataRepository of(final String filePath, final String fileName) {
        return new JsonRepository(Paths.get(filePath), fileName);
    }

    public static DataRepository of(final Path filePath, final String fileName) {
        return new JsonRepository(filePath, fileName);
    }

    public static BDRepository of(final String driverName,
                                  final String db,
                                  final String userName,
                                  final String password,
                                  final Session session) {
        try {
            Class driver = RepositoryFactory.class.forName(driverName);
            DriverManager.getConnection(db, userName, password);
            return new BDRepository(
                    DriverManager.getConnection(db, userName, password),
                    session
            );
        } catch (ClassNotFoundException e) {
            throw new UnableToConnectException("Le driver " + driverName + " n'a pas pu être chargé.", e);
        } catch (SQLException e) {
            throw new UnableToConnectException("Unable to acces db: " + db + "\nMessage: " + e.getMessage(), e);
        }
    }

    public static BDRepository of(final String driverName,
                                  final String db,
                                  final String userName,
                                  final String password,
                                  final String author) {
        try {
            Class driver = RepositoryFactory.class.forName(driverName);
            return new BDRepository(
                    new ConnectionFactory(db, userName, password),
                    author
            );
        } catch (ClassNotFoundException e) {
            throw new UnableToConnectException("Le driver " + driverName + " n'a pas pu être chargé.", e);
        }
    }

    public static BDRepository of(final String driverName,
                                  final String db,
                                  final String userName,
                                  final String password) {
        return of(driverName, db, userName, password, new Session());
    }

    public static RepositoryFactory get(final String filePath, final String fileName, final String author) {
        return new RepositoryFactory(true) {
            @Override
            public DataRepository newRepository() {
                final var result = RepositoryFactory.of(filePath, fileName);
                result.setCurrentAuthor(author);
                return result;
            }
        };
    }

    public static RepositoryFactory get(final String driverName,
                                        final String db,
                                        final String userName,
                                        final String password,
                                        final String author) {
        return new RepositoryFactory(false) {
            @Override
            public DataRepository newRepository() {
                return RepositoryFactory.of(driverName,
                        db,
                        userName,
                        password,
                        author);
            }
        };
    }
}
