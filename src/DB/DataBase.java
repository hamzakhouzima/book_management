package DB;

import java.sql.*;


public class DataBase{
    public static Connection  dbSetup() {
        String url = "jdbc:mysql://127.0.0.1:3306/creation_queries";
        String username = "root";
        String password = "";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");

            // Perform database operations here

        } catch (SQLException e) {
            // Handle any database connection errors
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Handle any errors that occur while closing the connection
                e.printStackTrace();
            }
        }
        return connection;

    }
}