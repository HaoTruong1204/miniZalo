package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import server.DatabaseConnectionModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientInfo;
    private int userId = -1; // Mặc định là -1 để kiểm tra sau này
    private String userEmail;

    // Constructor khởi tạo ClientHandler với thông tin socket và server
    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo luồng vào/ra cho client: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Nhận thông tin user từ client
            String initialMessage = in.readLine();
            if (initialMessage != null && initialMessage.startsWith("USER:")) {
                handleUserLogin(initialMessage); // Xử lý thông tin đăng nhập
            } else {
                sendMessage("ERROR:Invalid user information.");
                closeConnection(); // Đóng kết nối nếu thông tin không hợp lệ
                return;
            }

            // Gửi thông báo chào mừng tới client sau khi đăng nhập thành công
            sendMessage("Chào mừng bạn đến với server chat!");

            // Lắng nghe tin nhắn từ client
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message); // Xử lý tin nhắn từ client
            }
        } catch (IOException e) {
            System.err.println("Client " + clientInfo + " đã ngắt kết nối bất ngờ.");
        } finally {
            handleLogout(); // Xử lý ngắt kết nối hoặc đăng xuất
        }
    }

    // Phương thức để xử lý thông tin đăng nhập của người dùng
    private void handleUserLogin(String message) {
        userEmail = message.substring(5); // Tách email từ message
        userId = getUserIdFromEmail(userEmail);
        if (userId == -1) {
            System.err.println("Không tìm thấy user với email: " + userEmail);
            sendMessage("ERROR:User not found.");
            closeConnection();
        } else {
            System.out.println("Client đăng nhập với email: " + userEmail + ", userId: " + userId);
            server.broadcast(userEmail + " đã tham gia phòng chat.", this); // Thông báo tới các client khác
        }
    }

    // Xử lý tin nhắn từ client
    private void handleMessage(String message) {
        if ("LOGOUT".equals(message)) { // Nếu client gửi lệnh LOGOUT
            handleLogout();
        } else {
        	int receiverId = -1;
        	Pattern pattern = Pattern.compile("(\\d+).*:(\\w+)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String number = matcher.group(1); // "213"
                String word = matcher.group(2);   // "Hello"

                receiverId = Integer.parseInt(number);
                message = word;
            } else {
                System.out.println("Pattern not found!");
                return;
            }
            System.out.println("Nhận từ " + userEmail + ": " + message);
            saveMessageToDatabase(userId, receiverId, message); // Lưu tin nhắn vào cơ sở dữ liệu
            server.broadcast(userEmail + ": " + message, this); // Phát tin nhắn tới các client khác
        }
    }

    // Phương thức để xử lý khi người dùng đăng xuất
    private void handleLogout() {
        if (userEmail != null) {
            System.out.println("Người dùng " + userEmail + " đăng xuất.");
            server.removeClient(this); // Loại bỏ client khỏi danh sách server
            server.broadcast(userEmail + " đã rời khỏi phòng chat.", this); // Thông báo tới các client khác
        }
        closeConnection(); // Đóng kết nối với client
    }

    // Phương thức gửi tin nhắn tới client
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("Đã gửi tin nhắn tới client " + clientInfo + ": " + message);
        } else {
            System.err.println("Không thể gửi tin nhắn - Output stream không tồn tại.");
        }
    }

    // Phương thức đóng kết nối với client
    public void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            System.out.println("Đã đóng kết nối với client: " + clientInfo);
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối với client: " + e.getMessage());
        }
    }

    // Phương thức lấy thông tin client
    public String getClientInfo() {
        return clientInfo;
    }

    // Phương thức để lấy userId từ email
    private int getUserIdFromEmail(String email) {
        DatabaseConnectionModel dbConnection = new DatabaseConnectionModel();
        System.out.println("received: " + email);
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy user_id từ email: " + e.getMessage());
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

    // Phương thức lưu tin nhắn vào cơ sở dữ liệu
    private void saveMessageToDatabase(int senderId, int receiverId, String content) {
        DatabaseConnectionModel dbConnection = new DatabaseConnectionModel();
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();
            System.out.println("Đã lưu tin nhắn vào cơ sở dữ liệu.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu tin nhắn vào cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
