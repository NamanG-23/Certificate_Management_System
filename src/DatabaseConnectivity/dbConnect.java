package DatabaseConnectivity;
import java.sql.Connection;
import java.sql.DriverManager;
public class dbConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/certificate_management_system";
    private static final String USERNAME = "Enter_your_username";
    private static final String PASSWORD = "Enter_your_password";
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    URL,
                    USERNAME,
                    PASSWORD
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}