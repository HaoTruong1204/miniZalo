package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.UserModel;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;
    
    @FXML
    private Label loginLabel;
    
    @FXML
    private Button sendButton;

    private UserModel userModel = new UserModel();

    @FXML
    private void initialize() {
        sendButton.setOnAction(event -> handleForgotPassword());
        loginLabel.setOnMouseClicked(event -> redirectToLogin());
    }

    private void handleForgotPassword() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Thông báo", "Vui lòng nhập email của bạn.");
            return;
        }

        // Kiểm tra xem email có tồn tại không
        boolean emailExists = userModel.isEmailRegistered(email);
        if (emailExists) {
            // Gửi email khôi phục mật khẩu
            sendPasswordResetEmail(email);
            showAlert("Thông báo", "Yêu cầu khôi phục mật khẩu đã được gửi đến email của bạn.");
        } else {
            showAlert("Thông báo", "Email không tồn tại trong hệ thống.");
        }
    }

    private void sendPasswordResetEmail(String email) {
        // Ở đây, bạn cần tích hợp với một dịch vụ gửi email như JavaMail API hoặc một dịch vụ email bên thứ ba
        System.out.println("Gửi email khôi phục mật khẩu đến " + email);
        // Implement the actual email sending code here
    }

   
    
 /// Phương thức điều hướng về màn hình đăng nhập
    private void redirectToLogin() {
        try {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            Scene loginScene = new Scene(FXMLLoader.load(getClass().getResource("/view/LoginView.fxml")));
            stage.setScene(loginScene); // Đặt cảnh hiện tại thành loginScene
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
