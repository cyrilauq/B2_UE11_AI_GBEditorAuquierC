package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.infrastructures.exception.UnableToConnectException;
import org.helmo.gbeditor.infrastructures.jdbc.BDRepository;
import org.helmo.gbeditor.infrastructures.jdbc.ConnectionFactory;
import org.helmo.gbeditor.infrastructures.jdbc.JsonRepository;
import org.helmo.gbeditor.repositories.DataRepository;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.helmo.gbeditor.infrastructures.jdbc.DBConfig.*;

/**
 * Cette classe permet d'instancier une factory pour repository et de créer des repository cohérent.
 */
public class RepositoryFactory {

    private final RepositoryType type;

    /**
     * Créer une nouvelle RepositoryFactory en spécifiant si la ressources utilisée est le fichier Json ou non.
     * Si ce n'est pas le fichier Json alors on considère que c'est la base de donnée.
     *
     * @param isJson    True si la ressources utilisée est le Json
     *                  False sinon.
     */
    public RepositoryFactory(final boolean isJson) {
        type = isJson ? RepositoryType.JSON : RepositoryType.MY_SQL_DB;
    }

    /**
     * Crée une nouvelle factory mémorisant le type de ressource qui sera utilisée.
     *
     * @param repoType  Type de ressources.
     */
    public RepositoryFactory(final RepositoryType repoType) {
        if(repoType == null) {
            throw new IllegalArgumentException("The repo type you give does not exist");
        }
        type = repoType;
    }
    /**
     * Crée un repository sur base de la configuration donnée lors de l'initialisation de la factory.
     *
     * @return  Un repository valide permettant d'interagir avec le type de ressources données en argument lors de l'initialisation de la factory.
     */
    public DataRepository newRepository() {
        if(type == RepositoryType.JSON) {
            return of(Paths.get(System.getProperty("user.home"), "ue36"), "e200106");
        } else if(type == RepositoryType.MY_SQL_DB) {
            return of(DRIVER, DB_URL, DB_USER, DB_PASSWORD);
        }
        return of(DRIVER, LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASSWORD);
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

    /**
     * Crée un nouveau repository utilisant le Json dont le fichier se trouve dans le dossier filePath donné et avec le nom fileName donné.
     *
     * @param filePath  Chemin d'accès pour le dossier où se trouve le fichier.
     * @param fileName  Nom du fichier json.
     *
     * @return          Un repository permettant de travailler avec un fichier json.
     *                  Ce fichier se trouvera dans le dossier filePath donné et aura le nom fileName donné.
     */
    private static DataRepository of(final Path filePath, final String fileName) {
        return new JsonRepository(filePath, fileName);
    }

    /**
     * Crée un nouveau BDRepository à partir des informations données.
     *
     * @param driverName    Driver utilisé pour accéder à la base de données.
     * @param db            URL de la base de données
     * @param userName      Username pour se connecter à la base de données.
     * @param password      Mot de passe pour se connecter à la base de données.
     *
     * @return              Un BDRepository permettant de communiquer avec la base de données fournies.
     *
     * @throws UnableToConnectException Si le driver n'a pas pu être chargé.
     */
    public static BDRepository of(final String driverName,
                                  final String db,
                                  final String userName,
                                  final String password) {
        try {
           Class.forName(driverName);
            return new BDRepository(
                    new ConnectionFactory(db, userName, password)
            );
        } catch (ClassNotFoundException | UnableToConnectException e) {
            throw new UnableToConnectException("Le driver " + driverName + " n'a pas pu être chargé.", e);
        }
    }
}
