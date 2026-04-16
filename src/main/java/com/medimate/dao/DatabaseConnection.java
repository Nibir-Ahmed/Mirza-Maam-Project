package com.medimate.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton pattern for MySQL JDBC connection
 * Connects to local MySQL database
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Local MySQL credentials
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "medimate";
    private static final String USER = "root";
    private static final String PASSWORD = "root1234";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC";

    // Private constructor (Singleton)
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println(" MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println(" Database connection failed: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Reconnection failed: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}