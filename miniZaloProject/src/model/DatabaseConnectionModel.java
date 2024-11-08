package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnectionModel {
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/mini_zalo_db?useSSL=false&serverTimezone=UTC";
    private final String user = "root"; // Thay thế bằng tên người dùng MySQL của bạn
    private final String password = "H@opro37"; // Thay thế bằng mật khẩu MySQL của bạn

    // Phương thức thiết lập kết nối tới cơ sở dữ liệu
    public Connection connect() {
        if (isConnected()) {
            return connection;
        }
        try {
            // Nạp driver MySQL JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Kết nối tới cơ sở dữ liệu
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver MySQL JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Không thể kết nối đến cơ sở dữ liệu.");
            e.printStackTrace();
        }
        return connection;
    }

    // Phương thức ngắt kết nối khỏi cơ sở dữ liệu
    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Đã ngắt kết nối cơ sở dữ liệu!");
                }
            } catch (SQLException e) {
                System.err.println("Không thể ngắt kết nối cơ sở dữ liệu.");
                e.printStackTrace();
            }
        }
    }

    // Phương thức thực thi câu lệnh SELECT và trả về ResultSet
    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            if (!isConnected()) {
                connect();
            }
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            System.out.println("Thực thi câu lệnh truy vấn thành công!");
        } catch (SQLException e) {
            System.err.println("Lỗi khi thực thi câu lệnh truy vấn.");
            e.printStackTrace();
        }
        return rs;
    }

    // Phương thức thực thi câu lệnh INSERT, UPDATE, DELETE và trả về số hàng bị ảnh hưởng
    public int executeUpdate(String query) {
        int rowsAffected = 0;
        try {
            if (!isConnected()) {
                connect();
            }
            Statement stmt = connection.createStatement();
            rowsAffected = stmt.executeUpdate(query);
            System.out.println("Câu lệnh cập nhật thành công! Số hàng bị ảnh hưởng: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Lỗi khi thực thi câu lệnh cập nhật.");
            e.printStackTrace();
        }
        return rowsAffected;
    }

    // Kiểm tra trạng thái kết nối
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
