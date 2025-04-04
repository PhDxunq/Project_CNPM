package com.example.phongvanrestaurant.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    static DBConnection instance;
    public Connection cn;

    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        cn = null;

        try {
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pvrestaurant", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() throws ClassNotFoundException, SQLException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return cn;
    }
}
