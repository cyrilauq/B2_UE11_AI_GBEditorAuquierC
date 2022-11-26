package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.infrastructures.dto.BookDTO;

import java.sql.*;
import java.util.ArrayList;

/**
 * S'occupe de certaines des opérations en base se données liées aux livres.
 */
public class BookBDRepository {
    protected static BookDTO convertResultSetToDTO(final Connection connection, final ResultSet rs) throws SQLException {
        var isbn = rs.getString("isbn");
        var publishDate = rs.getTimestamp("datePublication");
        var result = new BookDTO(rs.getString("title"),
                isbn,
                rs.getString("author"),
                rs.getString("resume"),
                rs.getString("imgPath"),
                BookDTO.CURRENT_VERSION, new ArrayList<>(),
                publishDate == null ? null : publishDate.toLocalDateTime());
        result.id = rs.getInt("id_book");
        return result;
    }

    /**
     * Ajoute un DTO à un PreparedStament donné.
     *
     * @param dto       DTO à ajouter au PreparedStament.
     * @param saveStmt  PreparedStatement à utiliser.
     *
     * @throws SQLException
     */
    protected static void addDtoToUpdateStmt(BookDTO dto, PreparedStatement saveStmt) throws SQLException {
        saveStmt.setString(1, dto.getTitle());
        saveStmt.setString(2, dto.getResume());
        saveStmt.setString(3, dto.getIsbn());
        saveStmt.setString(4, dto.getImgPath());
        saveStmt.setString(5, dto.getAuthor());
        if(dto.getPublishDate() == null) {
            saveStmt.setTimestamp(6, null);
        } else {
            saveStmt.setTimestamp(6, Timestamp.valueOf(dto.getPublishDate()));
        }
        saveStmt.setInt(7, dto.id);
    }

    /**
     * Insère le DTO donné au PreparedStatement donné pour pouvoir le sauvegarde en base de donnée plus tard.
     *
     * @param dto       DTO à sauvegarder
     * @param saveStmt  PreparedStatement permettant la sauvegarde du DTO
     *
     * @throws SQLException
     */
    protected static void addDtoToInserStmt(BookDTO dto, PreparedStatement saveStmt) throws SQLException {
        saveStmt.setString(1, dto.getTitle());
        saveStmt.setString(2, dto.getIsbn());
        saveStmt.setString(3, dto.getResume());
        saveStmt.setString(4, dto.getImgPath());
        saveStmt.setString(5, dto.getAuthor());
    }

    protected static BookDTO convertResultSetToDTO(final PreparedStatement stmt) throws SQLException {
        try(final var rs = stmt.executeQuery()) {
            while (rs.next() && !rs.wasNull()) {
                return convertResultSetToDTO(stmt.getConnection(), rs);
            }
        }
        return null;
    }
}
