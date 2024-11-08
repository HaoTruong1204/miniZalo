package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> onMessageReceived; // Callback khi nhận tin nhắn
    private Runnable onConnectionLost; // Callback khi mất kết nối
    private volatile boolean connected; // Trạng thái kết nối
    private String userEmail;

    // Constructor để khởi tạo kết nối
    public ClientConnection(String serverAddress, int port, String userEmail) throws IOException {
        this.userEmail = userEmail;
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            System.out.println("Kết nối tới server thành công.");

            // Gửi thông tin người dùng tới server
            sendMessage("USER:" + userEmail);
//            sendMessage("EMAIL:" + userEmail);
        } catch (IOException e) {
            connected = false;
            System.err.println("Lỗi khi kết nối tới server: " + e.getMessage());
            throw e; // Ném ngoại lệ để xử lý bên ngoài
        }
    }

    // Bắt đầu lắng nghe tin nhắn từ server
    public void startListening() {
        new Thread(() -> {
            try {
                String message;
                while (connected && (message = in.readLine()) != null) {
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(message); // Callback khi nhận tin nhắn
                    }
                }
            } catch (IOException e) {
                System.err.println("Mất kết nối tới server: " + e.getMessage());
            } finally {
                handleConnectionLost(); // Xử lý khi mất kết nối
            }
        }).start();
    }

    // Thiết lập callback khi nhận tin nhắn
    public void setOnMessageReceived(Consumer<String> callback) {
        this.onMessageReceived = callback;
    }

    // Thiết lập callback khi mất kết nối
    public void setOnConnectionLost(Runnable callback) {
        this.onConnectionLost = callback;
    }

    // Gửi tin nhắn tới server
    public synchronized void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        } else {
            System.err.println("Không thể gửi tin nhắn - Kết nối đã đóng.");
        }
    }

    // Đăng xuất khỏi server và đóng kết nối
    public synchronized void logout() {
        if (connected) {
            sendMessage("LOGOUT"); // Gửi lệnh đăng xuất
            close(); // Đóng kết nối
        }
    }

    // Kiểm tra trạng thái kết nối
    public boolean isConnected() {
        return connected && !socket.isClosed();
    }

    // Đóng kết nối
    public synchronized void close() {
        if (!connected) {
            return;
        }
        connected = false;
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng input stream: " + e.getMessage());
        }
        try {
            if (out != null) out.close();
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng output stream: " + e.getMessage());
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng socket: " + e.getMessage());
        }
        System.out.println("Đã đóng kết nối tới server.");
    }

    // Xử lý khi mất kết nối
    private void handleConnectionLost() {
        connected = false;
        if (onConnectionLost != null) {
            onConnectionLost.run(); // Callback khi mất kết nối
        }
        close(); // Đóng tài nguyên
    }
}
