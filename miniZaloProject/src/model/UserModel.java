package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserModel {

    private final DatabaseConnectionModel dbConnection = new DatabaseConnectionModel(); // Kết nối đến cơ sở dữ liệu

    // Phương thức đăng ký người dùng mới
    public boolean register(String username, String email, String password) {
        if (isEmailRegistered(email)) {
            System.out.println("Email đã được đăng ký.");
            return false; // Email đã tồn tại
        }

        String hashedPassword = hashPassword(password); // Băm mật khẩu

        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
            System.out.println("Đăng ký thành công!");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký: " + e.getMessage());
            return false;
        }
    }

    // Phương thức đăng nhập người dùng
    public boolean login(String email, String password) {
        String sql = "SELECT password FROM users WHERE email = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String inputHashedPassword = hashPassword(password);

                if (storedHashedPassword.equals(inputHashedPassword)) {
                    System.out.println("Đăng nhập thành công!");

                    // Cập nhật trạng thái is_online thành TRUE
                    String updateOnlineStatusSql = "UPDATE users SET is_online = TRUE WHERE email = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateOnlineStatusSql)) {
                        updateStmt.setString(1, email);
                        updateStmt.executeUpdate();
                    }

                    return true; // Đăng nhập thành công
                } else {
                    System.out.println("Sai mật khẩu.");
                    return false; // Sai mật khẩu
                }
            } else {
                System.out.println("Email không tồn tại.");
                return false; // Email không tồn tại
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            return false;
        }
    }

    // Phương thức đăng xuất người dùng
    public void setOffline(String email) {
        String sql = "UPDATE users SET is_online = FALSE WHERE email = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.executeUpdate();
            System.out.println("Người dùng đã đăng xuất.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng xuất: " + e.getMessage());
        }
    }

    // Phương thức kiểm tra email đã đăng ký
    public boolean isEmailRegistered(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có bản ghi nào đó thì email đã được đăng ký
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra email: " + e.getMessage());
            return false;
        }
    }

    // Phương thức lấy thông tin người dùng dựa trên email
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, username, email FROM users WHERE email = ?";
        User user = null;

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                user = new User(userId, username, email); // Tạo đối tượng User
                System.out.println("Lấy thông tin người dùng thành công.");
            } else {
                System.out.println("Không tìm thấy người dùng với email này.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
        return user; // Trả về đối tượng User
    }

    // Phương thức cập nhật thông tin người dùng
    public boolean updateUserInfo(int userId, String newUsername, String newEmail, String newPassword) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE user_id = ?";

        String hashedPassword = hashPassword(newPassword); // Băm mật khẩu mới

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);
            stmt.setString(2, newEmail);
            stmt.setString(3, hashedPassword);
            stmt.setInt(4, userId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cập nhật thông tin người dùng thành công.");
                return true; // Cập nhật thành công
            } else {
                System.out.println("Cập nhật thông tin thất bại.");
                return false; // Không có bản ghi nào được cập nhật
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            return false;
        }
    }

    // Phương thức đặt lại mật khẩu
    public boolean resetPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";

        String hashedPassword = hashPassword(newPassword); // Băm mật khẩu mới

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Đặt lại mật khẩu thành công.");
                return true; // Đặt lại mật khẩu thành công
            } else {
                System.out.println("Không tìm thấy người dùng với email này.");
                return false; // Không tìm thấy email
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            return false;
        }
    }

    // Phương thức xóa tài khoản người dùng
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Tài khoản đã được xóa thành công.");
                return true; // Xóa tài khoản thành công
            } else {
                System.out.println("Không tìm thấy tài khoản để xóa.");
                return false; // Không có tài khoản nào để xóa
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tài khoản người dùng: " + e.getMessage());
            return false;
        }
    }

    // Lấy danh sách bạn bè cho người dùng đã đăng nhập
    public List<String> getFriendsList(String userEmail) {
        String sql = "SELECT u.username FROM friends AS f " +  
                     "JOIN users AS u ON f.friend_id = u.user_id " +
                     "JOIN users AS me ON f.user_id = me.user_id " +
                     "WHERE me.email = ? AND f.status = 'accepted'"; 

        List<String> friends = new ArrayList<>(); 

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("getFriendList: " + userEmail);

            while (rs.next()) {
                String friendUsername = rs.getString("username").trim();
                if (!userEmail.equals(friendUsername)) {
                	friends.add(friendUsername);
                }
            }

            System.out.println("Lấy danh sách bạn bè thành công.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bạn bè: " + e.getMessage());
        }
        return friends;
    }

    // Phương thức lấy danh sách người dùng trực tuyến
    public List<String> getOnlineUsers(String userEmail) {
        String sql = "SELECT username FROM users WHERE is_online = TRUE";

        List<String> onlineUsers = new ArrayList<>(); 

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                if (userEmail.equals(userEmail)) {
                	continue;
                }
                onlineUsers.add(username);
            }

            System.out.println("Lấy danh sách người dùng trực tuyến thành công.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách người dùng trực tuyến: " + e.getMessage());
        }
        return onlineUsers;
    }

    // Phương thức lấy lịch sử trò chuyện giữa người dùng và bạn bè
    public List<String> getChatHistory(String userEmail, String friendName) {
        String sql = "SELECT content FROM messages AS m " +
                     "JOIN users AS u1 ON m.sender_id = u1.user_id " +
                     "JOIN users AS u2 ON m.receiver_id = u2.user_id " +
                     "WHERE (u1.email = ? AND u2.username = ?) " +
                     "OR (u1.username = ? AND u2.email = ?) " +
                     "ORDER BY m.timestamp";

        List<String> chatHistory = new ArrayList<>();

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setString(2, friendName);
            stmt.setString(3, friendName);
            stmt.setString(4, userEmail);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String messageContent = rs.getString("content");
                chatHistory.add(messageContent);
            }

            System.out.println("Lấy lịch sử trò chuyện thành công.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử trò chuyện: " + e.getMessage());
        }
        return chatHistory;
    }

    // Phương thức băm mật khẩu sử dụng SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi khi băm mật khẩu: " + e.getMessage());
        }
    }

    // Lớp nội bộ để biểu diễn thông tin người dùng
    public static class User {
        private final int userId;
        private final String username;
        private final String email;

        public User(int userId, String username, String email) {
            this.userId = userId;
            this.username = username;
            this.email = email;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return "User{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

	public int getId(String userName) {
		String sql = "SELECT user_id FROM users WHERE username = ?";
		System.out.println(userName);

		   try (Connection conn = dbConnection.connect();
		        PreparedStatement stmt = conn.prepareStatement(sql)) {
		
		       stmt.setString(1, userName);
		
		       ResultSet rs = stmt.executeQuery();
		
		       if (rs.next()) {
		    	   String a = rs.getString("user_id");
		    	   return Integer.parseInt(a);
		       }
		
		   } catch (SQLException e) {
			   e.printStackTrace();
		       System.err.println("Lỗi khi lấy id user: " + e.getMessage());
		   }
		  return -1;
	}
}
