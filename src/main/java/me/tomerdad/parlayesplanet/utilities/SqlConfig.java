package me.tomerdad.parlayesplanet.utilities;

import me.tomerdad.parlayesplanet.ParlaYesPlanet;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class SqlConfig {

    private static Connection connection;
    private static Statement statement;
    private static String dbFileName = "players.db";


    public static String SQLiteCreateFoundRingsTable = "CREATE TABLE IF NOT EXISTS ringsFound (" +
            "`id` integer PRIMARY KEY," +
            "`player` varchar(32) NOT NULL," +
            "`ring` int(11) NOT NULL" +
            ");";

    public static String SQLiteCreateRingsTable = "CREATE TABLE IF NOT EXISTS rings (" +
            "`id` integer PRIMARY KEY," +
            "`ring` int(11) NOT NULL," +
            "`world` varchar(60) NOT NULL," +
            "`locX` float(11) NOT NULL," +
            "`locY` float(11) NOT NULL," +
            "`locZ` float(11) NOT NULL" +
            ");";


    public static synchronized Connection getSQLConnection() {
        File folder = new File(ParlaYesPlanet.getPlugin().getDataFolder(), dbFileName);
        if (!folder.exists()) {
            try {
                folder.createNewFile();
            } catch (IOException e) {
                ParlaYesPlanet.getPlugin().getLogger().log(Level.SEVERE, "File write error: " + dbFileName);
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + folder);
            return connection;
        } catch (SQLException ex) {
            ParlaYesPlanet.getPlugin().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            ParlaYesPlanet.getPlugin().getLogger().log(Level.SEVERE,
                    "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public static void Close() {
//        connection = getSQLConnection();
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
//        connection = getSQLConnection();
        try {
            statement = connection.createStatement();
            statement.executeUpdate(SQLiteCreateFoundRingsTable);
            statement.executeUpdate(SQLiteCreateRingsTable);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ResultSet selectQuery(String sql){

        try {
            statement = connection.createStatement();
            return statement.executeQuery(sql);

        } catch (SQLException e) {
            ParlaYesPlanet.getPlugin().getLogger().log(Level.WARNING, "Error");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static synchronized void updateQuery(String sql){

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static synchronized Integer getCount(String sql){

        try {
            statement = connection.createStatement();
            ResultSet data = statement.executeQuery(sql);
            int count = data.getInt(1);
            statement.close();
            return count;

        } catch (SQLException e) {
            ParlaYesPlanet.getPlugin().getLogger().log(Level.WARNING, "Error");
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static synchronized Boolean checkIfNull(String sql){
        return getCount(sql) == 0;
    }
}
