package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 */
/**
 * 
 */
public class ServerTest {

    public static void main(String[] args) {
        // Khởi chạy server trên một luồng riêng
        new Thread(() -> {
            try {
                Server server = new Server(12345); // Cổng của server phải khớp với cổng của client
                server.start();
            } catch (IOException e) {
                System.err.println("Lỗi khi khởi động server: " + e.getMessage());
            }
        }).start();

        // Tạm dừng một chút để đảm bảo server đã khởi động trước khi client kết nối
        try {
            Thread.sleep(2000); // Chờ 2 giây để server khởi động, có thể điều chỉnh nếu cần
        } catch (InterruptedException e) {
            System.err.println("Lỗi khi chờ server khởi động: " + e.getMessage());
        }

        // Tạo nhiều client để kiểm tra server với nhiều kết nối
        int clientCount = 3; // Số lượng client để kiểm thử
        for (int i = 0; i < clientCount; i++) {
            new Thread(ServerTest::connectAndCommunicateWithServer).start();
        }
    }

    // Phương thức để mỗi client kết nối và gửi tin nhắn đến server
    private static void connectAndCommunicateWithServer() {
        try (Socket clientSocket = new Socket("127.0.0.1", 12345);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            int clientPort = clientSocket.getLocalPort();
            System.out.println("Client " + clientPort + " đã kết nối tới server thành công!");

            // Gửi tin nhắn từ client đếnX server
            String message = "Hello from client " + clientPort;
            out.println(message);
            System.out.println("Client " + clientPort + " đã gửi tin nhắn: " + message);

            // Đọc và hiển thị phản hồi từ server
            String response = in.readLine();
            if (response != null) {
                System.out.println("Client " + clientPort + " nhận phản hồi từ server: " + response);
            } else {
                System.out.println("Client " + clientPort + " không nhận được phản hồi từ server.");
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi kết nối tới server từ client: " + e.getMessage());
        }
    }
}
