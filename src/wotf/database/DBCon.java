package wotf.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wessel on 1-2-2016.
 */
public class DBCon {
    private static Connection connection;

    // TODO: 6-3-2016 Refactor this so that the username and password are loaded from a file instead of being hard-coded
    private static String connectionPath = "ourConnectionString"; // example oracle string: "jdbc:oracle:thin:@localhost:1521:XE";
    private static String username = "username";
    private static String password = "password";

    private static String numericDefault = "0";

    // Static block that gets executed the moment anything from this class gets called
    // This block is no longer necessary in the newer versions of JDBC
    /*static {
        try {
            //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println("Registered Oracle OJDBC driver");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    public static Connection getConnection()
    {
        try {
            connection = DriverManager.getConnection(connectionPath, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return connection;
    }

    public static int ExecuteUpdate(String query) { return ExecuteUpdate(query, null); }
    public static int ExecuteUpdate(String query, List<Object> parameters) {
        Connection con = getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareCall(query);

            // If the list of parameters isn't empty, adds them to the prepared statement
            if (parameters != null) {
                for (int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            int result = ps.executeUpdate();
            if(result != 0) {
                return result;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // If we've reached this point, it means the query didn't succeed, return -1
        return -1;
    }

    public static ResultSet ExecuteResultSet(String query)
    {
        return ExecuteResultSet(query, null);
    }

    public static ResultSet ExecuteResultSet(String query, List<Object> parameters)
    {
        Connection con = getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareCall(query);

            // If the list of parameters isn't empty, adds them to the prepared statement
            if(parameters != null)
            {
                for(int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            // If the query returned a ResultSet, we can safely return it (or at least the first one)
            if(ps.execute()) {
                return ps.getResultSet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If we've reached this point, it means the query didn't succeed, return null
        return null;
    }

    public static List<String[]> ExecuteResultSetAsList(String query)
    {
        return ExecuteResultSetAsList(query, null);
    }
    public static List<String[]> ExecuteResultSetAsList(String query, List<Object> parameters)
    {
        Connection con = getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareCall(query);

            // If the list of parameters isn't empty, adds them to the prepared statement
            if(parameters != null)
            {
                for(int i = 1; i <= parameters.size(); i++) {
                    ps.setObject(i, parameters.get(i - 1));
                }
            }

            // If the query returned a ResultSet, we can start parsing it
            if(ps.execute()) {
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
                                    record[i-1] = numericDefault;
                                    continue;
                            }

                            record[i-1] = "";
                        }
                        else {
                            record[i-1] = value.toString();
                        }
                    }

                    result.add(record);
                }

                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If we've reached this point, it means the query didn't succeed, return null
        return null;
    }
}