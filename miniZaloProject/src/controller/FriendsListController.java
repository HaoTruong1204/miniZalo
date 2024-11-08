package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import model.UserService;
import app.SceneManager;
import javafx.stage.Stage;
import java.util.List;
import java.io.IOException;

public class FriendsListController {

    @FXML
    private Label titleLabel;

    @FXML
    private ScrollPane friendsScrollPane;

    @FXML
    private ListView<String> friendsListView; // Dùng String làm ví dụ

    @FXML
    private Button backButton;

    @FXML
    private Button addFriendButton;

    private final UserService userService = new UserService(); // Khởi tạo UserService

    @FXML
    private void initialize() {
        // Thiết lập sự kiện cho các nút
        backButton.setOnAction(event -> handleBackButton());
        addFriendButton.setOnAction(event -> handleAddFriend());
        loadFriendsList(); // Nạp danh sách bạn bè
    }

    // Xử lý khi nhấn nút 'Quay Lại'
    private void handleBackButton() {
        System.out.println("Quay lại màn hình chính");
        SceneManager.getInstance().popScene(); // Quay lại trang trước đó
    }

    // Xử lý khi nhấn nút 'Thêm Bạn'
    private void handleAddFriend() {
        System.out.println("Thêm bạn mới");
        loadPage("/view/AddFriendView.fxml", "Thêm Bạn"); // Tải giao diện thêm bạn
    }

    // Tải danh sách bạn bè từ UserService
    private void loadFriendsList() {
        String userEmail = getCurrentUserEmail(); // Lấy email của người dùng hiện tại
        List<String> friends = userService.getFriendsList(userEmail); // Lấy danh sách bạn bè từ UserService

        // Hiển thị danh sách bạn bè lên ListView
        friendsListView.getItems().clear(); // Xóa danh sách hiện tại
        friendsListView.getItems().addAll(friends); // Thêm danh sách bạn bè vào ListView

        // Thiết lập sự kiện khi chọn một bạn bè trong danh sách
        friendsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleFriendSelected(newSelection); // Xử lý khi bạn bè được chọn
            }
        });
    }

    // Xử lý khi một bạn bè được chọn từ danh sách
    private void handleFriendSelected(String friendName) {
        System.out.println("Đã chọn bạn: " + friendName);
        loadChatWithFriend(friendName); // Mở chat với bạn
    }

    // Giả lập phương thức để lấy email người dùng hiện tại
    private String getCurrentUserEmail() {
        // Trả về email của người dùng hiện tại
        return "user@example.com"; // Thay bằng cách lấy email từ UserService hoặc từ phiên làm việc
    }

    // Phương thức tải giao diện chat với bạn bè
    private void loadChatWithFriend(String friendName) {
        System.out.println("Mở chat với " + friendName);
        loadPage("/view/ChatView.fxml", "Trò Chuyện với " + friendName); // Thay đổi đường dẫn đến ChatView
    }

    // Phương thức để tải một trang khác với tiêu đề tùy chỉnh
    private void loadPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Chuyển đến trang mới
            SceneManager.getInstance().pushScene(view); // Sử dụng SceneManager để chuyển cảnh
            
            // Cập nhật tiêu đề của cửa sổ
            Stage stage = (Stage) backButton.getScene().getWindow(); // Lấy cửa sổ hiện tại từ nút backButton
            stage.setTitle(title); // Cập nhật tiêu đề
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể tải giao diện: " + fxmlPath); // Thông báo lỗi
        }
    }
}
