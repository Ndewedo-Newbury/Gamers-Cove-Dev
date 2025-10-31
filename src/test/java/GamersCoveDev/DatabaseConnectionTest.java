package GamersCoveDev;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/gamerscove";
        String user = "postgres";
        String password = "pass0000";

        System.out.println("Testing database connection...");
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ Successfully connected to the database!");
                System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("❌ Failed to make connection!");
            }
        } catch (Exception e) {
            System.err.println("❌ Connection failed! Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}