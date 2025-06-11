import java.sql.SQLException;

public class AuthManager {
    private UserRepository userRepository;

    public AuthManager() {
        this.userRepository = new UserRepository();
    }

    public User authenticate(String username, String password) {
        try {
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user != null) {
                System.out.println("Login berhasil sebagai " + user.getRole() + "!");
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error saat otentikasi: " + e.getMessage());
            // Dalam aplikasi nyata, log error ini ke file log
            return null;
        }
    }
}