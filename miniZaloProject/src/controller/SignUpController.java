package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.UserModel;

import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    private UserModel userModel = new UserModel();

    @FXML
    private void initialize() {
        signUpButton.setOnAction(event -> handleSignUp());
    }

    // Xử lý logic đăng ký người dùng
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Thông báo", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Lỗi", "Mật khẩu và mật khẩu xác nhận không khớp.");
            return;
        }

        boolean success = userModel.register(username, email, password);
        if (success) {
            showAlert("Thông báo", "Đăng ký thành công!");
            redirectToLogin(); // Chuyển về màn hình đăng nhập nếu đăng ký thành công
        } else {
            showAlert("Lỗi", "Đăng ký thất bại. Email có thể đã được sử dụng.");
        }
    }

    // Phương thức chuyển hướng về trang đăng nhập
    private void redirectToLogin() {
        try {
            Stage stage = (Stage) signUpButton.getScene().getWindow(); // Lấy stage hiện tại
            Parent loginView = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(loginView);
            stage.setScene(loginScene);
            stage.setTitle("miniZalo - Đăng Nhập"); // Đặt tiêu đề cho màn hình đăng nhập
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải màn hình đăng nhập. Vui lòng thử lại.");
        }
    }

    // Phương thức hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
