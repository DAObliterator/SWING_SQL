/*package DBConnection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/nba_db";
    private static final String USER = "root";
    private static final String PASSWORD = "timo@2003";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}*/
package DBConnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    public static void loadProperties() {
        Properties properties = new Properties();
        /*try (FileInputStream input = new FileInputStream("D:/SWING/NBAProject/NBAProject/src/main/resources") ) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            // Now use dbUrl, dbUser, dbPassword
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

        try {
            properties.load(new FileInputStream("D:/SWING/NBAProject/NBAProject/src/main/resources/application.properties"));
            dbUrl = properties.getProperty("db.url");
            dbPassword = properties.getProperty("db.password");
            dbUser = properties.getProperty("db.username");
        } catch (
            Exception e
        ) {
            System.out.println(e);
        }
    }

    // Method to establish and return the database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            loadProperties();  // Load properties if not already loaded
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}

