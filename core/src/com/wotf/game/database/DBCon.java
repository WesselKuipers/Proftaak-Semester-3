package com.wotf.game.database;

import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Helper class for making database calls
 */
public class DBCon {

    private static Connection connection;

    private static String ConnectionPath;
    private static String Username;
    private static String Password;

    private static final String NumericDefault = "0";

    /**
     * @return a JDBC Connection object based
     */
    public static Connection getConnection() {
        if (ConnectionPath == null) {
            loadProperties();
        }
        
        try {
            connection = DriverManager.getConnection(ConnectionPath, Username, Password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return connection;
    }

    /**
     * Loads the login info required to make a connection to the database
     */
    private static void loadProperties() {
        Properties prop = new Properties();
        try {
            prop.load(Gdx.files.internal("dbc.properties").read());
            
            ConnectionPath = prop.getProperty("ConnectionPath");
            Username = prop.getProperty("Username");
            Password = prop.getProperty("Password");
        } catch (IOException ex) {
            Gdx.app.error("Database", "Couldn't load dbc.properties");
        }
    }

    /**
     * Executes an update query
     * @param query Query to run
     * @return Number of rows afflicted
     */
    public static int executeUpdate(String query) {
        return executeUpdate(query, null);
    }

    /**
     * Executes an update query with parameters
     * @param query Query to run
     * @param parameters Parameters to add to the prepared statement
     * @return Number of rows afflicted
     */
    public static int executeUpdate(String query, List<Object> parameters) {
        Connection con = getConnection();
        try (PreparedStatement ps = con.prepareCall(query)) {

            // If the list of parameters isn't empty, adds them to the prepared statement
            if (parameters != null) {
                for (int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            int result = ps.executeUpdate();
            if (result != 0) {
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // If we've reached this point, it means the query didn't succeed, return -1
        return -1;
    }

    /**
     * Executes a query and returns the resulting ResultSet object
     * @param query Query to run
     * @return ResultSet of the query
     */
    public static ResultSet executeResultSet(String query) {
        return executeResultSet(query, null);
    }

     /**
     * Executes a query and returns the resulting ResultSet object
     * @param query Query to run
     * @param parameters Parameters to add to the prepared statement
     * @return ResultSet of the query
     */
    public static ResultSet executeResultSet(String query, List<Object> parameters) {
        Connection con = getConnection();
        
        try (PreparedStatement ps = con.prepareCall(query)) {
            // If the list of parameters isn't empty, adds them to the prepared statement
            if (parameters != null) {
                for (int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            // If the query returned a ResultSet, we can safely return it (or at least the first one)
            if (ps.execute()) {
                return ps.getResultSet();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // If we've reached this point, it means the query didn't succeed, return null
        return null;
    }

     /**
     * Executes a query and converts its result set as a List of Strings
     * @param query Query to run
     * @return List of String[] representing every row returned from the query
     */
    public static List<String[]> executeResultSetAsList(String query) {
        return executeResultSetAsList(query, null);
    }

    /**
     * Executes a query with parameters and converts its result set as a List of Strings
     * @param query Query to run
     * @param parameters Parameters to add to the prepared statement
     * @return List of String[] representing every row returned from the query
     */
    public static List<String[]> executeResultSetAsList(String query, List<Object> parameters) {
        Connection con = getConnection();

        try (PreparedStatement ps = con.prepareCall(query)) {

            // If the list of parameters isn't empty, adds them to the prepared statement
            if (parameters != null) {
                for (int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            // If the query returned a ResultSet, we can start parsing it
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                ResultSetMetaData metaData = ps.getMetaData();
                int columnCount = metaData.getColumnCount();

                List<String[]> result = new ArrayList<>();

                while (rs.next()) {
                    String[] record = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) { // We're starting with 1 here because columnIndexes
                        Object value = rs.getObject(i);      // are one-based, meaning they start with 1 instead of 0

                        // For defaulting numeric values, we want this to be set to 0, assuming 0 is the default value
                        if (value == null) {
                            switch (metaData.getColumnType(i)) {
                                case Types.BIGINT:
                                case Types.INTEGER:
                                case Types.DECIMAL:
                                case Types.FLOAT:
                                case Types.NUMERIC:
                                case Types.SMALLINT:
                                    record[i - 1] = NumericDefault;
                                    continue;
                            }

                            record[i - 1] = "";
                        } else {
                            record[i - 1] = value.toString();
                        }
                    }

                    result.add(record);
                }

                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // If we've reached this point, it means the query didn't succeed, return null
        return null;
    }
}
