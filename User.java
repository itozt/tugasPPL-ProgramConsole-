public class User {
    private int userId;
    private String username;
    private String password; // Plain-text sesuai permintaan
    private String role; // participant, observer, user_manager

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
