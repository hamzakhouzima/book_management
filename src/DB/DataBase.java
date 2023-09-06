package DB;

import java.sql.*;

public class DataBase {
    public static Connection dbSetup() {
        String url = "jdbc:mysql://127.0.0.1:3306/book_library_4";
        String username = "root";
        String password = "";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            // Handle any database connection errors
            e.printStackTrace();
        }

        return connection;
    }
}
