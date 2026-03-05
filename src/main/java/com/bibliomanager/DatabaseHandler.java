package com.bibliomanager;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHandler {
    public static Connection connectDB() {
        String url = "jdbc:sqlite:database.db";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
        }
        catch (Exception e) {
            System.err.println("ERROR : database connection failed : " + e.getMessage());
        }
        return conn;
    }
}
