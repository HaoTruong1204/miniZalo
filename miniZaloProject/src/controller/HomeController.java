package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserService;
import app.SceneManager;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import java.io.IOException;
import javafx.scene.Parent;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.input.KeyCode;
import network.ClientConnection;

public class HomeController {

    @FXML
    private ListView<String> friendsList; // Danh sách bạn bè

    @FXML
    private ListView<String> onlineUsersListView; // Danh sách người dùng trực tuyến

    @FXML
    private VBox chatArea; // Khu vực hiển thị tin nhắn

    @FXML
    private TextArea messageInput; // Ô nhập tin nhắn

    @FXML
    private Button sendButton; // Nút gửi tin nhắn

    @FXML
    private Button createGroupButton; // Nút Tạo Nhóm

    @FXML
    private Button settingsButton; // Nút Cài Đặt

    @FXML
    private Button logoutButton; // Nút Đăng Xuất

    @FXML
    private Button viewFriendsButton; // Nút Danh Sách Bạn Bè

    @FXML
    private Label footerLabel; // Footer hiển thị thông tin
    
    private int currentId = -1;

    private final UserService userService = new UserService(); // Khởi tạo UserService
    private String userEmail; // Biến để lưu trữ email người dùng
    private ClientController clientController; // Đối tượng ClientController quản lý kết nối tới server

    @FXML
    private void initialize() {
        setupListeners(); // Thiết lập sự kiện cho các nút
        footerLabel.setText("miniZalo © 2024"); // Đặt thông tin footer
        System.out.println("home load OK");
    }

    // Phương thức để thiết lập email người dùng và khởi tạo ClientController
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        clientController = new ClientController("127.0.0.1", 12345, userEmail); // Thay đổi server và cổng nếu cần
        clientController.setMessageListener(new ClientController.MessageListener() {
            @Override
            public void onMessageReceived(String message) {
                Platform.runLater(() -> {
                    Label receivedMessage = new Label("Server: " + message);
                    loadFriendsList();
                    loadOnlineUsers();
                    chatArea.getChildren().add(receivedMessage); // Hiển thị tin nhắn nhận được
                });
            }

            @Override
            public void onConnectionLost() {
                Platform.runLater(() -> {
                    System.out.println("Mất kết nối tới server.");
                    loadPage("/view/LoginView.fxml", "Đăng Nhập"); // Quay lại màn hình đăng nhập khi mất kết nối
                });
            }
        });

        loadFriendsList(); // Tải danh sách bạn bè sau khi nhận email người dùng
        loadOnlineUsers(); // Tải danh sách người dùng trực tuyến
    }

    // Thiết lập các sự kiện cho các nút và ô nhập tin nhắn
    private void setupListeners() {
        createGroupButton.setOnAction(event -> handleCreateGroup());
        settingsButton.setOnAction(event -> handleSettings());
        logoutButton.setOnAction(event -> handleLogout());
        sendButton.setOnAction(event -> sendMessage());
        viewFriendsButton.setOnAction(event -> loadFriendsListView());

        // Thêm sự kiện khi nhấn Enter trong ô nhập tin nhắn
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume(); // Ngăn chặn xuống dòng
                sendMessage();
            }
        });

        // Thiết lập sự kiện khi chọn bạn bè trong danh sách
        friendsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatHistory(newValue);
            }
        });

        // Thiết lập sự kiện khi chọn người dùng trực tuyến trong danh sách
        onlineUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatHistory(newValue);
            }
        });
    }

    // Tải danh sách bạn bè từ UserService và hiển thị lên giao diện
    private void loadFriendsList() {
        List<String> friends = userService.getFriendsList(userEmail);
        friendsList.getItems().clear();
        friendsList.getItems().addAll(friends);
    }

    // Tải danh sách người dùng trực tuyến từ UserService và hiển thị lên giao diện
    private void loadOnlineUsers() {
        List<String> onlineUsers = userService.getOnlineUsers(userEmail);
        onlineUsersListView.getItems().clear();
        onlineUsersListView.getItems().addAll(onlineUsers);
    }

    // Hiển thị danh sách bạn bè
    private void loadFriendsListView() {
        loadPage("/view/FriendsListView.fxml", "Danh Sách Bạn Bè");
    }

    // Hiển thị lịch sử trò chuyện cho bạn bè hoặc người dùng đã chọn
    private void loadChatHistory(String userName) {
        chatArea.getChildren().clear();
        List<String> chatHistory = userService.getChatHistory(userEmail, userName);
        for (String message : chatHistory) {
            Label chatMessage = new Label(message);
            chatArea.getChildren().add(chatMessage);
        }
        this.currentId = userService.getId(userName);
    }

    // Gửi tin nhắn
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) {
            System.out.println("Không thể gửi tin nhắn trống");
            return;
        }
        Label userMessage = new Label("Bạn: " + message);
        chatArea.getChildren().add(userMessage);
        messageInput.clear();
        
        if (clientController != null && clientController.isConnected()) {
            clientController.sendMessage(this.currentId + ":@?@?@?@:" + message); // Gửi tin nhắn tới server qua ClientController
        } else {
            System.out.println("Không thể gửi tin nhắn - Chưa kết nối tới server.");
        }
    }

    // Xử lý khi nhấn vào "Tạo Nhóm"
    private void handleCreateGroup() {
        loadPage("/view/GroupView.fxml", "Tạo Nhóm");
    }

    // Xử lý khi nhấn vào "Cài Đặt"
    private void handleSettings() {
        loadPage("/view/SettingsView.fxml", "Cài Đặt");
    }

    // Xử lý khi nhấn vào "Đăng Xuất"
    private void handleLogout() {
        if (clientController != null) {
            clientController.disconnect(); // Ngắt kết nối khi đăng xuất
        }
        userService.logout(userEmail);
        System.out.println("Đăng xuất thành công.");
        loadPage("/view/LoginView.fxml", "Đăng Nhập");
    }

    // Phương thức để tải một trang khác với tiêu đề tùy chỉnh
    private void loadPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            SceneManager.getInstance().pushScene(view);

            Stage stage = SceneManager.getInstance().getPrimaryStage();
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể tải giao diện: " + fxmlPath);
        }
    }
}
