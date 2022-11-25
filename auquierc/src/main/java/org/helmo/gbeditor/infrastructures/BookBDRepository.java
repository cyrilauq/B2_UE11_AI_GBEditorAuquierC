package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.helmo.gbeditor.infrastructures.jdbc.SQLInstructions.SELECT_CHOICES_FROM_PAGE_STMT;
import static org.helmo.gbeditor.infrastructures.jdbc.SQLInstructions.SELECT_PAGE_FROM_ISBN_STMT;

public class BookBDRepository {
    public static BookDTO convertResultSetToDTO(final Connection connection, final ResultSet rs) throws SQLException {
        var isbn = rs.getString("isbn");
        var publishDate = rs.getTimestamp("datePublication");
        var result = new BookDTO(rs.getString("title"),
                isbn,
                rs.getString("author"),
                rs.getString("resume"),
                rs.getString("imgPath"),
                BookDTO.CURRENT_VERSION, new ArrayList<>(getPageFor(connection, isbn)),
                publishDate == null ? null : publishDate.toLocalDateTime());
        result.id = rs.getInt("id_book");
        rs.close();
        return result;
    }

    private static Collection<PageDTO> getPageFor(final Connection connection, final String isbn) throws SQLException {
        final List<PageDTO> result = new ArrayList<>();
        try(PreparedStatement stmt = connection.prepareStatement(SELECT_PAGE_FROM_ISBN_STMT)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && !rs.wasNull()) {
                result.add(
                        new PageDTO(rs.getString("content"),
                                getChoicesFor(connection,
                                        rs.getInt("id_page")
                                ),
                                rs.getInt("num_page"))
                );
            }
            rs.close();
        }
        return result;
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
