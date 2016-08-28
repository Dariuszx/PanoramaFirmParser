package pl.parser.utils;


import pl.parser.Main;
import pl.parser.exceptions.ParserException;

import java.io.*;
import java.sql.*;

public class Database {

    // JDBC driver name and database URL
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static String DB_URL;

    //  Database credentials
    public static String USER;
    public static String PASS;


    private Connection connection;
    private Statement statement;

    public Database() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void DatabaseConfiguration(File file) throws ParserException {

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            if (Main.SHOW_STEPS) System.out.println("- rozpoczynam konfigurowanie polaczenia z baza danych");

            while ((line = br.readLine()) != null) {
                String[] vars = line.split("=");

                if(vars[0].equals("db")) {
                    Database.DB_URL = vars[1] + "?useUnicode=true&characterEncoding=utf8";
                }
                else if (vars[0].equals("username")) {
                    Database.USER = vars[1];
                }
                else if (vars[0].equals("password")) {
                    Database.PASS = vars[1];
                }
            }

            if(Database.DB_URL == null || Database.USER == null || Database.PASS == null) {
                throw new ParserException("Nie podano wszystkich parametrów bazy danych.");
            } else {
                if (Main.SHOW_STEPS) System.out.println("\tDB_URL=" + Database.DB_URL + "\n\tUSER=" + Database.USER + "\n\tPASS=*****");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createStatement(String sql) throws SQLException {
        statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    public ResultSet selectStatement(String sql) throws SQLException {
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public int getSize(ResultSet resultSet) {
        int size = 0;
        try {
            resultSet.last();
            size = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (Exception ex) {
            return 0;
        }
        return size;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
