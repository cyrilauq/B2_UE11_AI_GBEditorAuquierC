package org.helmo.gbeditor.infrastructures.jdbc;

public class DBConfig {

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Accès BD distante
    public static final String DB_URL = "jdbc:mysql://192.168.128.13:3306/in20b1001?useUnicode=true & " +
            "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false & " +
            "serverTimezone=UTC&useSSL=false";
    public final static String DB_USER = "in20b1001";
    public final static String DB_PASSWORD = "4918";

    // Accès à la DB local
    public final static String LOCAL_DB_URL = "jdbc:mysql://localhost:3306/gbreader?useUnicode=true & " +
            "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false & " +
            "serverTimezone=UTC&useSSL=false";
    public final static String LOCAL_DB_USER = "u823384744_cyril";
    public final static String LOCAL_DB_PASSWORD = "Ca5wsd0e@";

}
