package com.wotf.game.database;

import com.badlogic.gdx.Gdx;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;

/**
 * Created by Wessel on 1-2-2016.
 */
public class DBCon {

    private static Connection connection;

    // This hsould be refactored so that the username and password are loaded from a file instead of being hard-coded
    // not implemented because of syncing issues with version control
    // for implementation: see 
    private static final String ConnectionPath = "jdbc:mysql://145.93.176.159:3306/school";
    private static final String Username = "connect";
    private static final String Password = "admin";

    private static final String NumericDefault = "0";

    /**
     * @return a JDBC Connection object based
     */
    public static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(ConnectionPath, Username, Password);
        } catch (SQLException e) {
            Gdx.app.log("SQL", e.getMessage());
            return null;
        }

        return connection;
    }

    /**
     * Executes an update query
     *
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
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareCall(query);

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
            Gdx.app.log("SQL", e.getMessage());
        } finally {
            closeQuietly(con, ps);
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
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            con = getConnection();
            ps = con.prepareCall(query);

            // If the list of parameters isn't empty, adds them to the prepared statement
            if (parameters != null) {
                for (int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            // If the query returned a ResultSet, we can safely return it (or at least the first one)
            if (ps.execute()) {
                result = ps.getResultSet();
                
                CachedRowSet rowset;
                rowset = new CachedRowSetImpl();
                rowset.populate(result);

                return rowset;
            }
        } catch (SQLException e) {
            Gdx.app.log("SQL", e.getMessage());
        }finally{
            closeQuietly(con, ps, result);
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
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareCall(query);

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
                rs.close();
                return result;
            }
        } catch (SQLException e) {
            Gdx.app.log("SQL", e.getMessage());
        } finally {
            closeQuietly(con, ps);
        }
        
        // If we've reached this point, it means the query didn't succeed, return null
        return null;
    }
    
    /**
     * Closes the active connections with the database
     * @param closeables List of connections
     */
    public static void closeQuietly(AutoCloseable ... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    // log or ignore, we can't do anything about it really
                    Gdx.app.log("SQL", e.getMessage());
                }
            }
        }
    }
}
