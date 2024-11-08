package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 */
public class ClientConnectionTest {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    // Constructor để thiết lập kết nối với server
    public ClientConnectionTest(String serverAddress, int port) throws IOException {
        try {
            clientSocket = new Socket(serverAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Kết nối tới server thành công!");
        } catch (IOException e) {
            System.err.println("Lỗi khi kết nối tới server tại " + serverAddress + ":" + port + " - " + e.getMessage());
            throw e; // Ném lại ngoại lệ để caller có thể xử lý
        }
    }

    // Phương thức gửi tin nhắn tới server
    public void sendMessage(String message) {
        if (isConnected()) {
            out.println(message);
            System.out.println("Đã gửi tin nhắn: " + message);
        } else {
            System.out.println("Không thể gửi tin nhắn. Kết nối đã đóng.");
        }
    }

    // Phương thức nhận tin nhắn từ server
    public String receiveMessage() {
        try {
            if (isConnected()) {
                String response = in.readLine();
                if (response != null) {
                    return response; // Trả về tin nhắn nhận được
                } else {
                    System.out.println("Server đã ngắt kết nối.");
                    close();
                }
            } else {
                System.out.println("Không thể nhận tin nhắn. Kết nối đã đóng.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi nhận tin nhắn từ server: " + e.getMessage());
            close();
        }
        return null; // Nếu không nhận được tin nhắn, trả về null
    }

    // Kiểm tra trạng thái kết nối
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    // Phương thức đóng kết nối
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            System.out.println("Đã đóng kết nối tới server.");
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    // Chạy thử Client để kết nối, gửi tin nhắn, nhận phản hồi và đóng kết nối
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Địa chỉ server
        int port = 12345; // Port khớp với server

        try {
            // Khởi tạo client và kết nối đến server
            ClientConnectionTest client = new ClientConnectionTest(serverAddress, port);

            // Gửi tin nhắn đến server
            client.sendMessage("Hello, Server! Đây là một tin nhắn từ ClientConnectionTest.");

            // Nhận phản hồi từ server
            String response = client.receiveMessage();
            if (response != null) {
                System.out.println("Phản hồi từ server: " + response);
            }

            // Đợi một chút để đảm bảo nhận tin nhắn từ server
            Thread.sleep(2000); // Chờ 2 giây trước khi đóng kết nối

            // Đóng kết nối
            client.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi kết nối hoặc giao tiếp với server: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Lỗi khi chờ: " + e.getMessage());
        }
    }
}
