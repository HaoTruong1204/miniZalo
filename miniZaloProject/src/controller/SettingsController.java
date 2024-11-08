package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import app.SceneManager;

public class SettingsController {

    @FXML
    private CheckBox darkModeCheckBox; // Checkbox cho chế độ tối

    @FXML
    private CheckBox notificationsCheckBox; // Checkbox cho thông báo

    @FXML
    private Button changePasswordButton; // Nút đổi mật khẩu

    @FXML
    private Button backButton; // Nút quay lại trang Home

    // Phương thức khởi tạo
    @FXML
    private void initialize() {
        darkModeCheckBox.setOnAction(event -> toggleDarkMode());
        notificationsCheckBox.setOnAction(event -> toggleNotifications());
        changePasswordButton.setOnAction(event -> handleChangePassword());
        backButton.setOnAction(event -> handleBackToHome());
    }

    // Xử lý chuyển đổi chế độ tối
    private void toggleDarkMode() {
        if (darkModeCheckBox.isSelected()) {
            System.out.println("Chế độ tối đã được bật.");
            // Thực hiện chuyển đổi chế độ tối (áp dụng style)
        } else {
            System.out.println("Chế độ tối đã được tắt.");
            // Thực hiện chuyển đổi về chế độ sáng
        }
    }

    // Xử lý bật/tắt thông báo
    private void toggleNotifications() {
        if (notificationsCheckBox.isSelected()) {
            System.out.println("Thông báo đã được bật.");
            // Thực hiện bật thông báo
        } else {
            System.out.println("Thông báo đã được tắt.");
            // Thực hiện tắt thông báo
        }
    }

    // Xử lý khi người dùng nhấn nút Đổi mật khẩu
    private void handleChangePassword() {
        System.out.println("Đổi mật khẩu được nhấn.");
        // Thực hiện chuyển đổi sang giao diện đổi mật khẩu
        SceneManager.getInstance().pushScene("/view/ChangePasswordView.fxml");
    }

    // Xử lý quay lại trang Home
    private void handleBackToHome() {
        System.out.println("Quay lại trang chủ.");
        // Chuyển về HomeView bằng SceneManager
        SceneManager.getInstance().popScene(); // Sử dụng popScene để quay về giao diện trước
    }
}
