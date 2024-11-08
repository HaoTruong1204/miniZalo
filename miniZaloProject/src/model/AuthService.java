package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private final DatabaseConnectionModel dbConnection;

    // Constructor để khởi tạo AuthService với DatabaseConnectionModel
    public AuthService() {
        this.dbConnection = new DatabaseConnectionModel();
        dbConnection.connect();
    }

    // Phương thức để đăng nhập người dùng
    public boolean login(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            if (!dbConnection.isConnected()) {
                dbConnection.connect();
            }
            PreparedStatement stmt = dbConnection.connect().prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Đăng nhập thành công cho người dùng: " + email);
                return true;
            } else {
                System.out.println("Đăng nhập thất bại: Email hoặc mật khẩu không đúng.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thực hiện đăng nhập.");
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức để đăng ký người dùng mới
    public boolean register(String email, String username, String password) {
        // Kiểm tra xem email đã tồn tại chưa
        if (userExists(email)) {
            System.out.println("Email đã được sử dụng.");
            return false;
        }

        String query = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
        try {
            if (!dbConnection.isConnected()) {
                dbConnection.connect();
            }
            PreparedStatement stmt = dbConnection.connect().prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Đăng ký thành công cho người dùng: " + email);
                return true;
            } else {
                System.out.println("Đăng ký thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thực hiện đăng ký.");
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức kiểm tra xem email đã tồn tại chưa
    private boolean userExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try {
            if (!dbConnection.isConnected()) {
                dbConnection.connect();
            }
            PreparedStatement stmt = dbConnection.connect().prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Trả về true nếu email đã tồn tại
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra sự tồn tại của người dùng.");
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức để ngắt kết nối cơ sở dữ liệu khi không cần thiết
    public void close() {
        dbConnection.disconnect();
    }
}
