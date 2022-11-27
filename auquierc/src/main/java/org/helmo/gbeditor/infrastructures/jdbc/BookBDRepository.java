package org.helmo.gbeditor.infrastructures.jdbc;

import org.helmo.gbeditor.infrastructures.dto.BookDTO;

import java.sql.*;
import java.util.ArrayList;

/**
 * S'occupe de certaines des opérations en base se données liées aux livres.
 */
public class BookBDRepository {

    /**
     * Convertit un ResultSet donné en BTO.
     *
     * @param rs    ResultSet à convertir en DTO
     *
     * @return      Un DTO contenant les informations du ResultSet.
     *
     * @throws SQLException Si une des opérations avec la base de donnée a échoué.
     */
    protected static BookDTO convertResultSetToDTO(final ResultSet rs) throws SQLException {
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
     * @throws SQLException Si une des opérations avec la base de donnée a échoué.
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
     * @throws SQLException Si une des opérations avec la base de donnée a échoué.
     */
    protected static void addDtoToInserStmt(BookDTO dto, PreparedStatement saveStmt) throws SQLException {
        saveStmt.setString(1, dto.getTitle());
        saveStmt.setString(2, dto.getIsbn());
        saveStmt.setString(3, dto.getResume());
        saveStmt.setString(4, dto.getImgPath());
        saveStmt.setString(5, dto.getAuthor());
    }

    /**
     * Convertit un ResultSet, qui est le résultat d'un PreparedStatement donné, en DTO.
     *
     * @param stmt  PreparedStatement qui donnera le ResultSet à convertir.
     *
     * @return      La conversion en DTO du ResultSet si l'exécution du PreparedStatement a retourmé un résultat.
     *              Null si l'exécution du PreparedStatement ne contenait aucun résultat.
     *
     * @throws SQLException Si une des opérations avec la base de donnée a échoué.
     */
    protected static BookDTO convertResultSetToDTO(final PreparedStatement stmt) throws SQLException {
        try(final var rs = stmt.executeQuery()) {
            return rs.next() && !rs.wasNull() ? convertResultSetToDTO(rs) : null;
        }
    }
}
