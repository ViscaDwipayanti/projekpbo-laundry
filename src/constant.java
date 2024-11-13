import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class constant {
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/applaundry"; // Update with your database URL
            String user = "your_username"; // Update with your database username
            String password = "your_password"; // Update with your database password
            
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }
}
