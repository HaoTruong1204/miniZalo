package controller;

import model.UserModel;

public class UserController {
    private UserModel userModel;

    // Constructor
    public UserController() {
        userModel = new UserModel();
    }

    // Phương thức để xử lý đăng ký người dùng
    public boolean registerUser(String username, String email, String password) {
        if (username == null || username.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Thông tin đăng ký không hợp lệ.");
            return false;
        }

        boolean isRegistered = userModel.register(username, email, password);
        if (isRegistered) {
            System.out.println("Đăng ký thành công cho người dùng: " + username);
        } else {
            System.out.println("Đăng ký thất bại.");
        }
        return isRegistered;
    }

    // Phương thức để xử lý đăng nhập người dùng
    public boolean loginUser(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Thông tin đăng nhập không hợp lệ.");
            return false;
        }

        boolean isLoggedIn = userModel.login(email, password);
        if (isLoggedIn) {
            System.out.println("Đăng nhập thành công cho người dùng: " + email);
        } else {
            System.out.println("Đăng nhập thất bại.");
        }
        return isLoggedIn;
    }
}
