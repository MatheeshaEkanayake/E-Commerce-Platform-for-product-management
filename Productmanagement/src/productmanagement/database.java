/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package productmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class database {
        public static Connection initializeDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/e_management"; // Replace with your database URL
            String user = "root"; // Replace with your database username
            String password = ""; // Replace with your database password

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            return null;
        }
    }

}
