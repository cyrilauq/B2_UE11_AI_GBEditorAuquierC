package org.helmo.gbeditor.infrastructures.jdbc;

/**
 * Cette classe réunit toutes les requêtes SQL qui seront utilisées lors de l'utilisation du programme.
 */
public class SQLInstructions {

    public static final String CREATE_AUTHOR_STMT = "CREATE TABLE author(" +
            "id_author INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
            "name VARCHAR(250) NOT NULL," +
            "UNIQUE(name))";

    public static final String AUTHOR_EXISTS_STMT = "SELECT id_author " +
            "FROM author " +
            "WHERE name = ?";

    public static final String DROP_AUTHOR_STMT = "DROP TABLE author";

    public static final String CREATE_BOOK_STMT = "CREATE TABLE book(" +
            "id_book INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
            "title VARCHAR(150) NOT NULL, " +
            "imgPath VARCHAR(50), " +
            "isbn VARCHAR(11) NOT NULL, " +
            "resume VARCHAR(250) NOT NULL, " +
            "datePublication VARCHAR(50) DEFAULT NULL, " +
            "id_author INT DEFAULT NULL, " +
            "UNIQUE(isbn), " +
            "FOREIGN KEY(id_author) REFERENCES author(id_author))";

    public static final String CREATE_CHOICES_STMT = "CREATE TABLE choice(" +
            "id_choice INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
            "content VARCHAR(150) NOT NULL, " +
            "id_page INT NOT NULL, " +
            "id_target INT NOT NULL, " +
            "FOREIGN KEY(id_page) REFERENCES page(id_page) ON DELETE CASCADE, " +
            "FOREIGN KEY(id_target) REFERENCES page(id_page) ON DELETE CASCADE)";

    public static final String DROP_CHOICES_STMT = "DROP TABLE choice";

    public static final String DROP_BOOK_STMT = "DROP TABLE book";

    public static final String CREATE_PAGE_STMT = "CREATE TABLE page(" +
            "id_page INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
            "content VARCHAR(250) NOT NULL, " +
            "num_page INT DEFAULT 1, " +
            "id_book INT NOT NULL, " +
            "FOREIGN KEY(id_book) REFERENCES book(id_book) ON DELETE CASCADE)";

    public static final String INSERT_PAGE_STMT = "INSERT INTO page(content, id_book, num_page)" +
            "VALUES(?, ?, ?)";

    public static final String SELECT_PAGE_ID_STMT = "SELECT id_page " +
            "FROM page " +
            "WHERE content = ? AND id_book = ?";

    // TODO : Pas de modification de contenu de page

    public static final String SELECT_PAGE_ID_FROM_NUM_STMT = "SELECT id_page " +
            "FROM page " +
            "WHERE num_page = ? AND id_book = ?";

    public static final String DELETE_PAGE_FOR_ISBN_STMT = "DELETE FROM page " +
            "WHERE id_book = ?";

    public static final String UPDATE_PAGE_STMT = "UPDATE page SET " +
            "content = ?, " +
            "id_book = ? " +
            "WHERE content = ? AND id_book = ?";

    public static final String PAGE_EXISTS_STMT = "SELECT id_page " +
            "FROM page " +
            "WHERE content = ? AND id_book = ?";

    public static final String SELECT_ID_BOOK_STMT = "SELECT id_book " +
            "FROM book " +
            "WHERE isbn = ?";

    public static final String DROP_PAGE_STMT = "DROP TABLE page";

    public static final String SELECT_ALL_BOOKS_STMT = "SELECT b.id_book, b.title, b.resume, b.isbn, b.imgPath, " +
            "a.name AS author, b.datePublication " +
            "FROM book b " +
            "JOIN author a ON a.id_author = b.id_author " +
            "WHERE a.name = ?";

    public static final String SELECT_BOOKS_WITH_ISBN_STMT = "SELECT b.id_book, b.title, b.resume, b.isbn, b.imgPath, " +
            "b.datePublication, " +
            "(SELECT a.name FROM author a WHERE a.id_author = b.id_author) AS author " +
            "FROM book b " +
            "WHERE b.isbn = ?";

    public static final String DELETE_BOOKS_WITH_ISBN_STMT = "DELETE FROM book " +
            "WHERE isbn = ?";

    public static final String UPDATE_BOOKS_STMT = "UPDATE book SET " +
            "title = ?, resume = ?, isbn = ?, imgPath = ?, " +
            "datePublication = ? " +
            "WHERE id_book = ?";

    public static final String INSERT_BOOK_STMT = "INSERT INTO book(title, isbn, resume, imgPath, id_author) " +
            "VALUES(?, ?, ?, ?, " +
            "(SELECT a.id_author " +
                "FROM author a " +
                "WHERE a.name = ?))";

    public static final String INSERT_AUTHOR_STMT = "INSERT INTO author(name) " +
            "VALUES(?)";

    public static final String DELETE_PAGE_FROM_BOOK_STMT = "DELETE FROM page " +
            "WHERE id_book = ?";

    public static final String SELECT_PAGE_FROM_ISBN_STMT = "SELECT " +
            "content, num_page, id_page " +
            "FROM page " +
            "WHERE id_book = (SELECT b.id_book FROM book b WHERE b.isbn = ?) " +
            "ORDER BY num_page";

    public static final String SELECT_CHOICES_FROM_PAGE_STMT = "SELECT " +
            "c.content, pTo.num_page AS targetP, " +
            "pFrom.num_page AS pFrom " +
            "FROM choice c " +
            "JOIN page pTo ON pTo.id_page = c.id_target " +
            "JOIN page pFrom ON pFrom.id_page = c.id_page " +
            "WHERE c.id_page = ?";

    public static final String INSERT_CHOICES_STMT = "INSERT INTO choice(content, id_page, id_target) " +
            "VALUES(?, ?, ?)";

    public static final String SELECT_LAST_ISBN_STMT = "SELECT isbn " +
            "FROM book " +
            "ORDER BY isbn DESC " +
            "LIMIT 1";

}
