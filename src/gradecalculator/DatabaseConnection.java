package gradecalculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {

    // ── ✏️  EDIT THESE TO MATCH YOUR MYSQL SETUP ──────────────────────────
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "grade_calculator";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "aquayena99";   // ← put your MySQL password here
    // ───────────────────────────────────────────────────────────────────────

    private static final String DB_URL =
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            return conn;

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "❌ MySQL JDBC Driver NOT found!\n\n" +
                "Fix: Right-click project → Properties → Libraries\n" +
                "     → Add JAR → select mysql-connector-j-8.3.0.jar",
                "Driver Missing", JOptionPane.ERROR_MESSAGE);

        } catch (SQLException e) {
            String msg = e.getMessage();
            String hint;

            if (msg.contains("Access denied")) {
                hint = "❌ Wrong username or password!\n\n" +
                       "Open DatabaseConnection.java and fix:\n" +
                       "  DB_USER = \"root\"\n" +
                       "  DB_PASS = \"your_actual_password\"";
            } else if (msg.contains("Unknown database")) {
                hint = "❌ Database 'grade_calculator' does not exist!\n\n" +
                       "Fix: Open MySQL Workbench and run database_setup.sql first.";
            } else if (msg.contains("Communications link failure") || msg.contains("refused")) {
                hint = "❌ Cannot reach MySQL server!\n\n" +
                       "Fix: Press Win+R → type services.msc → Enter\n" +
                       "     Find 'MySQL80' → Right-click → Start";
            } else {
                hint = "❌ Connection failed!\n\nMySQL said: " + msg;
            }

            JOptionPane.showMessageDialog(null, hint, "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}