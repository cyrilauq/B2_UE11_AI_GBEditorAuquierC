package org.helmo.gbeditor.infrastructures.jdbc;

import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;
import org.helmo.gbeditor.repositories.exceptions.DataManipulationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.helmo.gbeditor.infrastructures.jdbc.SQLInstructions.*;

/**
 * S'occupe de certaines des opérations en base se données liées aux pages d'un livre
 */
public class PageBDRepository {

    /**
     * Ajoute les pages d'un livre à un PreparedStatement.
     *
     * @param dto   Livre qui contient les pages qu'on souhaite ajouter
     * @param stmt  Contient la requête pour l'ajout des pages.
     *
     * @throws SQLException Si une erreur SQL s'est produite.
     */
    protected static void addPageToStmt(final BookDTO dto, final PreparedStatement stmt) throws SQLException {
        for(final var p : dto) {
            addPageToStmt(p, dto.id, stmt);
        }
        stmt.executeBatch();
    }

    /**
     * Ajoute les choix d'une page à une page en base de données.
     *
     * @param connection    Connection à la base de données.
     * @param dto           Itérable contenant les pages que l'on souhaite ajouter.
     * @param id_book       identifiant du livre en base de donnée, auquel les pages appartiennent.
     *
     * @throws SQLException Si une erreur SQL s'est produite.
     */
    protected static void addChoicesToPages(final Connection connection, final Iterable<PageDTO> dto, final int id_book) throws SQLException {
        try(final var stmt = connection.prepareStatement(INSERT_CHOICES_STMT)) {
            for(final var page : dto) {
                page.getChoices().forEach((c, p) -> {
                    try {
                        stmt.setString(1, c);
                        stmt.setInt(2, getIdPageFor(connection, page.getContent(), id_book));
                        stmt.setInt(3, getIdPageFor(connection, p, id_book));
                        stmt.addBatch();
                    } catch (SQLException e) {
                        throw new DataManipulationException("Une erreur est survenue lors de la sauvegarde des choix du livre.", e);
                    }
                });
                stmt.executeBatch();
            }
        }
    }

    /**
     * Ajoute une page donnée à un PreparedStatement donné.
     *
     * @param dto       Page à ajouter en base de donnée.
     * @param id_book   identifiant en base de données du livre auquel appartient la page.
     * @param stmt      PreparedStatement permettant l'ajout en base de données de la page.
     *
     * @throws SQLException Si une erreur SQL s'est produite.
     */
    protected static void addPageToStmt(final PageDTO dto, final int id_book, final PreparedStatement stmt) throws SQLException {
        if(id_book != -1) {
            stmt.setString(1, dto.getContent());
            stmt.setInt(2, id_book);
            stmt.setInt(3, dto.getNumPage());
            stmt.addBatch();
        }
    }

    private static int getIdPageFor(final Connection connection, final String content, final int id_book) {
        try(PreparedStatement loadStmt = connection.prepareStatement(SELECT_PAGE_ID_STMT)) {
            loadStmt.setString(1, content);
            loadStmt.setInt(2, id_book);
            return getFirstKey(loadStmt);
        } catch (SQLException e) {
            throw new DataManipulationException(e);
        }
    }

    /**
     * Récupère la première clef, entière, résultant de l'exécution d'un PreparedStatement donné.
     *
     * @param loadStmt  PreparedStatement duquel on veut récupérer la première clef.
     *
     * @return          Si l'exécution retourne au moins un tuple, la première clef entière résultant de cette exécution.
     *
     * @throws SQLException Si une erreur SQL s'est produite.
     */
    protected static int getFirstKey(PreparedStatement loadStmt) throws SQLException {
        try(final var keys = loadStmt.executeQuery()) {
            if(keys.next() && !keys.wasNull()) {
                return keys.getInt(1);
            }
            return -1;
        }
    }

    /**
     * Récupère les pages provenant d'un PreparedStatement donné.
     *
     * @param stmt  PreparedStatement qui contient les pages à récupérée.
     *
     * @return      Une liste de page.
     *
     * @throws SQLException Si une erreur SQL s'est produite.
     */
    protected static List<PageDTO> getPageFromStmt(final PreparedStatement stmt) throws SQLException {
        final List<PageDTO> result = new ArrayList<>();
        try(final var rs = stmt.executeQuery()) {
            while (rs.next() && !rs.wasNull()) {
                result.add(convertResultSetToDTO(rs));
            }
        }
        return result;
    }

    private static PageDTO convertResultSetToDTO(final ResultSet rs) throws SQLException {
        return new PageDTO(rs.getString("content"),
                getChoicesFor(rs.getStatement().getConnection(),
                        rs.getInt("id_page")
                ),
                rs.getInt("num_page")
        );
    }

    private static Map<String, String> getChoicesFor(final Connection connection, final int id_page) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(SELECT_CHOICES_FROM_PAGE_STMT)) {
            stmt.setInt(1, id_page);
            return getChoicesFrom(stmt);
        }
    }

    private static Map<String, String> getChoicesFrom(final PreparedStatement stmt) throws SQLException {
        try(final var rs = stmt.executeQuery()) {
            final Map<String, String> result = new TreeMap<>();
            while (rs.next() && !rs.wasNull()) {
                result.put(rs.getString("content"), rs.getInt("targetP") + "");
            }
            return result;
        }
    }

}
