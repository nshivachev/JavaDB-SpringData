package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {
    private static Connection connection;

    private Connector() {}

    private static void CreateConnection(String dbName) throws SQLException {
        if (connection != null) return;

        final Properties properties = new Properties();
        properties.setProperty(Constants.USER_KEY, Constants.USER_VALUE);
        properties.setProperty(Constants.PASSWORD_KEY, Constants.PASSWORD_VALUE);

        connection = DriverManager.getConnection(Constants.JDBC_URL + dbName + Constants.PARAMETER_FOR_THE_DATABASE, properties);
    }
    public static Connection getSQLConnection(String dbName) throws SQLException {
        CreateConnection(dbName);
        return connection;
    }
}
