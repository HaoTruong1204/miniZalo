package controller;

import java.io.IOException;
import network.ClientConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController {
    private ClientConnection clientConnection;
    private final String serverAddress;
    private final int serverPort;
    private final ExecutorService executorService;
    private String userEmail;
    private MessageListener messageListener; // Listener để nhận sự kiện từ client

    // Constructor khởi tạo ClientController với thông tin server và userEmail
    public ClientController(String serverAddress, int serverPort, String userEmail) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.userEmail = userEmail;
        this.executorService = Executors.newSingleThreadExecutor();

        // Kết nối tới server khi khởi tạo ClientController
        connect();
    }

    // Interface để xử lý các sự kiện nhận tin nhắn và mất kết nối
    public interface MessageListener {
        void onMessageReceived(String message);
        void onConnectionLost();
    }

    // Thiết lập MessageListener
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    // Phương thức kết nối tới server
    public synchronized void connect() {
        if (clientConnection == null || !clientConnection.isConnected()) {
            try {
                clientConnection = new ClientConnection(serverAddress, serverPort, userEmail);
                System.out.println("Kết nối tới server thành công qua ClientController.");

                // Đặt callback khi nhận tin nhắn từ server
                clientConnection.setOnMessageReceived(this::handleReceivedMessage);

                // Đặt callback khi mất kết nối
                clientConnection.setOnConnectionLost(this::handleConnectionLost);

                // Bắt đầu lắng nghe tin nhắn từ server
                clientConnection.startListening();

            } catch (IOException e) {
                System.err.println("Không thể kết nối tới server: " + e.getMessage());
            }
        } else {
            System.out.println("Đã kết nối tới server.");
        }
    }

    // Phương thức để xử lý khi nhận tin nhắn từ server
    private void handleReceivedMessage(String message) {
        System.out.println("Đã nhận tin nhắn từ server: " + message);

        // Xử lý thông báo lỗi từ server
        if (message.startsWith("ERROR:")) {
            System.err.println("Lỗi từ server: " + message);
            // Thêm mã xử lý, ví dụ: thông báo trên giao diện người dùng
        } else if (messageListener != null) {
            // Chuyển tiếp tin nhắn cho MessageListener để xử lý trên giao diện
            messageListener.onMessageReceived(message);
        }
    }

    // Phương thức để xử lý khi mất kết nối
    private void handleConnectionLost() {
        System.out.println("Mất kết nối tới server.");
        if (messageListener != null) {
            // Chuyển tiếp sự kiện mất kết nối tới MessageListener
            messageListener.onConnectionLost();
        }
    }

    // Phương thức gửi tin nhắn
    public synchronized void sendMessage(String message) {
        if (clientConnection != null && clientConnection.isConnected()) {
            executorService.submit(() -> {
                clientConnection.sendMessage(message);
                System.out.println("Đã gửi tin nhắn: " + message);
            });
        } else {
            System.out.println("Không thể gửi tin nhắn - Chưa kết nối tới server.");
        }
    }

    // Kiểm tra trạng thái kết nối
    public boolean isConnected() {
        return clientConnection != null && clientConnection.isConnected();
    }

    // Đóng kết nối
    public synchronized void disconnect() {
        if (clientConnection != null) {
            clientConnection.close();
            executorService.shutdownNow();
            System.out.println("Đã đóng kết nối tới server qua ClientController.");
        } else {
            System.out.println("Chưa có kết nối nào để đóng.");
        }
    }

    // Khởi tạo ClientController khi bắt đầu ứng dụng
    public static void main(String[] args) {
        // Giả sử bạn đã có userEmail từ quá trình đăng nhập
        String userEmail = "admin@example.com"; // Thay thế bằng email tồn tại trong cơ sở dữ liệu
        ClientController clientController = new ClientController("127.0.0.1", 12345, userEmail);

        // Gửi tin nhắn ví dụ
        clientController.sendMessage("Hello, Server!");

        // Simulate waiting for message to test receiving callback (optional)
        try {
            Thread.sleep(5000); // Chờ đợi để kiểm tra nhận tin nhắn từ server
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Ngắt kết nối khi không cần nữa
        clientController.disconnect();
    }
}
