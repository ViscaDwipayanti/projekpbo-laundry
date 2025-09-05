package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/applaundry"; // Ganti dengan URL database Anda
                String user = "root"; // Ganti dengan username database Anda
                String password = ""; // Ganti dengan password database Anda
                
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi berhasil!");
            } catch (SQLException e) {
                System.out.println("Koneksi gagal: " + e.getMessage());
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE,null, e);
                
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Koneksi ditutup.");
            } catch (SQLException e) {
                System.out.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}
