package model;

import java.util.List;

public class UserService {

    private final UserModel userModel; // Đối tượng UserModel để truy cập dữ liệu người dùng

    public UserService() {
        this.userModel = new UserModel();
    }

    // Đăng ký người dùng mới
    public boolean registerUser(String username, String email, String password) {
        if (userModel.isEmailRegistered(email)) {
            System.out.println("Email đã được đăng ký.");
            return false; // Trả về false nếu email đã tồn tại
        }
        boolean isRegistered = userModel.register(username, email, password);
        if (isRegistered) {
            System.out.println("Đăng ký thành công!");
        } else {
            System.out.println("Đăng ký thất bại.");
        }
        return isRegistered;
    }

    // Đăng nhập người dùng
    public boolean loginUser(String email, String password) {
        boolean isLoggedIn = userModel.login(email, password);
        if (isLoggedIn) {
            System.out.println("Đăng nhập thành công!");
        } else {
            System.out.println("Đăng nhập thất bại. Kiểm tra email và mật khẩu.");
        }
        return isLoggedIn;
    }

    // Cập nhật thông tin người dùng
    public boolean updateUser(int userId, String newUsername, String newEmail, String newPassword) {
        boolean isUpdated = userModel.updateUserInfo(userId, newUsername, newEmail, newPassword);
        if (isUpdated) {
            System.out.println("Cập nhật thông tin thành công!");
        } else {
            System.out.println("Cập nhật thông tin thất bại.");
        }
        return isUpdated;
    }

    // Kiểm tra email đã đăng ký
    public boolean isEmailRegistered(String email) {
        boolean exists = userModel.isEmailRegistered(email);
        if (exists) {
            System.out.println("Email đã được đăng ký.");
        } else {
            System.out.println("Email chưa được đăng ký.");
        }
        return exists;
    }

    // Đặt lại mật khẩu
    public boolean resetPassword(String email, String newPassword) {
        boolean isReset = userModel.resetPassword(email, newPassword);
        if (isReset) {
            System.out.println("Đặt lại mật khẩu thành công.");
        } else {
            System.out.println("Không tìm thấy email này.");
        }
        return isReset;
    }

    // Xóa tài khoản người dùng
    public boolean deleteUser(int userId) {
        boolean isDeleted = userModel.deleteUser(userId);
        if (isDeleted) {
            System.out.println("Tài khoản đã bị xóa.");
        } else {
            System.out.println("Xóa tài khoản thất bại.");
        }
        return isDeleted;
    }

    // Phương thức đăng xuất
    public void logout(String email) {
        userModel.setOffline(email); // Đặt trạng thái người dùng thành offline
        System.out.println("Người dùng đã đăng xuất.");
    }

    // Lấy danh sách bạn bè của người dùng
    public List<String> getFriendsList(String userEmail) {
        List<String> friends = userModel.getFriendsList(userEmail);
        if (friends != null) {
            System.out.println("Lấy danh sách bạn bè thành công.");
        } else {
            System.out.println("Không thể lấy danh sách bạn bè.");
        }
        return friends;
    }

    // Lấy danh sách người dùng trực tuyến
    public List<String> getOnlineUsers(String userEmail) {
        List<String> onlineUsers = userModel.getOnlineUsers(userEmail);
        if (onlineUsers != null) {
            System.out.println("Lấy danh sách người dùng trực tuyến thành công.");
        } else {
            System.out.println("Không thể lấy danh sách người dùng trực tuyến.");
        }
        return onlineUsers;
    }

    // Lấy lịch sử trò chuyện giữa người dùng và bạn bè
    public List<String> getChatHistory(String userEmail, String friendName) {
        // Kiểm tra các tham số đầu vào để đảm bảo không bị null
        if (userEmail == null || friendName == null || userEmail.isEmpty() || friendName.isEmpty()) {
            System.out.println("Thông tin người dùng hoặc bạn bè không hợp lệ.");
            return null;
        }
        
        List<String> chatHistory = userModel.getChatHistory(userEmail, friendName);
        if (chatHistory != null) {
            System.out.println("Lấy lịch sử trò chuyện thành công.");
        } else {
            System.out.println("Không thể lấy lịch sử trò chuyện.");
        }
        return chatHistory;
    }

	public int getId(String userName) {
		// TODO Auto-generated method stub
		return userModel.getId(userName);
	}
}
