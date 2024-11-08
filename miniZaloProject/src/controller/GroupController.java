package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import app.SceneManager;
import model.UserService;
import java.util.ArrayList;
import java.util.List;

public class GroupController {

    @FXML
    private TextField groupNameField; // Ô nhập tên nhóm

    @FXML
    private ListView<String> friendsList; // Danh sách bạn bè để chọn thành viên

    @FXML
    private Button createGroupButton; // Nút Tạo Nhóm

    @FXML
    private Button backButton; // Nút quay lại trang chủ

    private List<String> selectedMembers; // Danh sách thành viên được chọn
    private final UserService userService = new UserService(); // Khởi tạo UserService để lấy danh sách bạn bè

    @FXML
    private void initialize() {
        selectedMembers = new ArrayList<>();
        loadFriendsList();

        // Thiết lập sự kiện cho nút Tạo Nhóm
        createGroupButton.setOnAction(event -> handleCreateGroup());
        
        // Thiết lập sự kiện cho nút quay lại
        backButton.setOnAction(event -> handleBackToHome());
        
        // Xử lý sự kiện khi chọn bạn bè trong danh sách
        friendsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !selectedMembers.contains(newValue)) {
                selectedMembers.add(newValue);
                System.out.println("Đã chọn: " + newValue);
            }
        });
    }

    // Tải danh sách bạn bè từ UserService vào ListView
    private void loadFriendsList() {
        // Giả sử userEmail là email của người dùng hiện tại đã đăng nhập
        String userEmail = "user@example.com"; // Lấy email của người dùng hiện tại từ biến phù hợp

        List<String> friends = userService.getFriendsList(userEmail); // Lấy danh sách bạn bè từ UserService
        friendsList.getItems().addAll(friends);
    }

    // Xử lý khi nhấn nút Tạo Nhóm
    private void handleCreateGroup() {
        String groupName = groupNameField.getText().trim();

        if (groupName.isEmpty()) {
            System.out.println("Vui lòng nhập tên nhóm.");
            return;
        }

        if (selectedMembers.isEmpty()) {
            System.out.println("Vui lòng chọn ít nhất một thành viên.");
            return;
        }

        System.out.println("Tạo nhóm thành công: " + groupName);
        System.out.println("Thành viên: " + selectedMembers);

        // Thực tế sẽ lưu thông tin nhóm vào database hoặc gửi đến server để xử lý
        // Ví dụ: GroupService.createGroup(groupName, selectedMembers);

        // Sau khi tạo nhóm thành công, có thể quay lại trang Home
        handleBackToHome();
    }

    // Xử lý quay lại trang Home
    private void handleBackToHome() {
        System.out.println("Quay lại trang chủ.");
        SceneManager.getInstance().popScene(); // Sử dụng popScene để quay lại giao diện trước
    }
}
