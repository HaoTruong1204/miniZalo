package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionModel {
    private final String url = "jdbc:mysql://localhost:3306/mini_zalo_db?useSSL=false&serverTimezone=UTC";
    private final String user = "root"; // Thay thế bằng tên người dùng MySQL của bạn
    private final String password = "H@opro37"; // Thay thế bằng mật khẩu MySQL của bạn

    private Connection connection = null;
    
    public DatabaseConnectionModel() {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }

    // Phương thức kết nối cơ sở dữ liệu
    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Kết nối tới cơ sở dữ liệu thành công.");
            } catch (SQLException e) {
                System.err.println("Không thể kết nối tới cơ sở dữ liệu: " + e.getMessage());
                throw e; // Ném ngoại lệ để xử lý bên ngoài nếu cần
            }
        }
        return connection;
    }

    // Phương thức đóng kết nối cơ sở dữ liệu
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối tới cơ sở dữ liệu.");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối tới cơ sở dữ liệu: " + e.getMessage());
            } finally {
                connection = null; // Đảm bảo connection được đặt lại về null sau khi đóng
            }
        }
    }

    // Phương thức kiểm tra trạng thái kết nối
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra trạng thái kết nối: " + e.getMessage());
            return false;
        }
    }
}
