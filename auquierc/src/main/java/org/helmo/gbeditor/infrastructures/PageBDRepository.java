package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;
import org.helmo.gbeditor.infrastructures.exception.DataManipulationException;

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
 * S'occupe des opérations en base se données liées aux pages d'un livre
 */
public class PageBDRepository {
    protected static void addPageToStmt(final BookDTO dto, final PreparedStatement stmt) throws SQLException {
        for(final var p : dto) {
            addPageToStmt(p, dto.id, stmt);
        }
        stmt.executeBatch();
    }

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

    protected static int getFirstKey(PreparedStatement loadStmt) throws SQLException {
        try(final var keys = loadStmt.executeQuery()) {
            if(keys.next() && !keys.wasNull()) {
                return keys.getInt(1);
            }
            return -1;
        }
    }

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
        final Map<String, String> result = new TreeMap<>();
        try(PreparedStatement stmt = connection.prepareStatement(SELECT_CHOICES_FROM_PAGE_STMT)) {
            stmt.setInt(1, id_page);
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && !rs.wasNull()) {
                result.put(rs.getString("content"), rs.getInt("targetP") + "");
            }
            rs.close();
        }
        return result;
    }

}
