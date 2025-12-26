package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/mentor_connect_app";
            String user = System.getenv("DB_USER");        // Get username from environment
            String password = System.getenv("DB_PASSWORD"); // Get password from environment
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
        return conn;
    }
}