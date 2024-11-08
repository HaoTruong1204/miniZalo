package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.UserModel;
import app.SceneManager;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label forgotPasswordLabel;
    @FXML
    private Label signUpLabel;

    private final UserModel userModel = new UserModel(); // Khởi tạo UserModel

    @FXML
    private void initialize() {
        // Thiết lập sự kiện cho các nút và nhãn
        loginButton.setOnAction(event -> handleLogin());
        forgotPasswordLabel.setOnMouseClicked(event -> handleForgotPassword());
        signUpLabel.setOnMouseClicked(event -> handleSignUp());
    }

    // Phương thức xử lý đăng nhập
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ email và mật khẩu.");
            return; // Dừng lại nếu thông tin đăng nhập trống
        }

        // Kiểm tra đăng nhập
        if (userModel.login(email, password)) {
            System.out.println("Đăng nhập thành công!");
            loadHomePage(email); // Điều hướng đến trang chính khi đăng nhập thành công
        } else {
            System.out.println("Email hoặc mật khẩu không đúng.");
            // Hiển thị thêm thông báo nếu cần thiết
        }
    }

    // Phương thức tải trang chính (HomePage) với truyền dữ liệu người dùng
    private void loadHomePage(String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HomeView.fxml"));
            Parent homeView = loader.load();

            HomeController homeController = loader.getController();
            homeController.setUserEmail(userEmail); // Cập nhật thông tin người dùng

            SceneManager.getInstance().pushScene(homeView); // Chuyển sang giao diện HomeView
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể tải giao diện trang chính. Vui lòng kiểm tra đường dẫn của HomeView.fxml.");
        }
    }

    // Xử lý khi người dùng nhấn "Quên mật khẩu"
    private void handleForgotPassword() {
        loadPage("/view/ForgotPasswordView.fxml", "Quên Mật Khẩu");
    }

    // Xử lý khi người dùng nhấn "Đăng ký"
    private void handleSignUp() {
        loadPage("/view/SignUpView.fxml", "Đăng Ký Tài Khoản");
    }

    // Phương thức để tải một trang khác với tiêu đề tùy chỉnh
    private void loadPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            SceneManager.getInstance().pushScene(view); // Sử dụng SceneManager để chuyển cảnh
            Stage stage = SceneManager.getInstance().getPrimaryStage(); // Lấy Stage từ SceneManager

            if (stage != null) { // Kiểm tra nếu stage không null
                stage.setTitle(title);
            } else {
                System.err.println("Stage chưa được khởi tạo.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể tải giao diện: " + fxmlPath);
        }
    }
}
