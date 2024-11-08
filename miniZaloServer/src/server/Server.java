package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clients = new ArrayList<>();
        System.out.println("Server khởi động thành công trên cổng " + port);
    }

    // Phương thức để bắt đầu server và lắng nghe kết nối từ client
    public void start() {
        System.out.println("Server đang chạy và chờ kết nối từ client...");

        while (!serverSocket.isClosed()) {
            try {
                // Chấp nhận kết nối từ client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới kết nối từ: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Tạo ClientHandler mới và thêm vào danh sách client
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                synchronized (clients) {
                    clients.add(clientHandler);
                }

                // Chạy ClientHandler trên một luồng riêng
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.err.println("Lỗi khi kết nối client: " + e.getMessage());
            }
        }
    }

    // Phương thức để phát tin nhắn đến tất cả các client, trừ client gửi tin
    public void broadcast(String message, ClientHandler sender) {
        System.out.println("Phát tin nhắn từ " + sender.getClientInfo() + ": " + message); // Thông báo debug
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) { // Không gửi lại cho client gửi tin nhắn
                    client.sendMessage(message);
                }
            }
        }
    }

    // Loại bỏ client khỏi danh sách khi ngắt kết nối
    public void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
            System.out.println("Đã ngắt kết nối với client: " + clientHandler.getClientInfo());
            broadcast("Client " + clientHandler.getClientInfo() + " đã rời khỏi phòng chat.", clientHandler);
        }
    }

    // Đóng server và giải phóng tài nguyên
    public void stop() {
        try {
            // Đóng tất cả các client
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.closeConnection(); // Đóng kết nối cho tất cả các client
                }
                clients.clear(); // Xóa danh sách client sau khi đóng kết nối
            }

            // Đóng server socket nếu nó chưa đóng
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Đóng server socket
            }
            System.out.println("Server đã ngừng hoạt động.");
        } catch (IOException e) {
            System.err.println("Lỗi khi dừng server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 12345; // Đảm bảo port này không bị xung đột
        try {
            Server server = new Server(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Lỗi khi khởi động server: " + e.getMessage());
        }
    }
}
