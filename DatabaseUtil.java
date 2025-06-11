import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// --- DatabaseUtil.java ---
public class DatabaseUtil {

    // DETAIL KONEKSI DATABASE ANDA
    private static final String URL = "jdbc:postgresql://localhost:5432/PVT";
    private static final String USER = "postgres";
    private static final String PASSWORD = "christo888"; // PERINGATAN: Password plain-text

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            // Driver akan otomatis terdaftar di JDBC 4.0+ (Java 6+), tapi ini tidak ada salahnya
            // Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("Koneksi ke database berhasil."); // Debugging
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database PostgreSQL:");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("Error: " + e.getMessage());
            throw e; // Lemparkan kembali exception agar ditangani pemanggil
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                // System.out.println("Koneksi database ditutup."); // Debugging
            } catch (SQLException e) {
                System.err.println("Error menutup koneksi database: " + e.getMessage());
            }
        }
    }
}
