package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.DatabaseConnectionModel;

public class App extends Application {

    private static DatabaseConnectionModel dbConnection;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Khởi tạo và khởi động SceneManager
            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.initialize(primaryStage);

            // Đẩy scene đầu tiên (LoginView) thông qua SceneManager
            sceneManager.pushScene("/view/LoginView.fxml");

            // Thiết lập tiêu đề cho cửa sổ ứng dụng
            primaryStage.setTitle("miniZalo - Đăng Nhập");
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Không thể tải giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Đảm bảo ngắt kết nối cơ sở dữ liệu khi ứng dụng đóng
        if (dbConnection != null && dbConnection.isConnected()) {
            dbConnection.disconnect();
            System.out.println("Ngắt kết nối cơ sở dữ liệu thành công.");
        }
    }

    public static void main(String[] args) {
        // Khởi tạo kết nối cơ sở dữ liệu
        dbConnection = new DatabaseConnectionModel();
        if (dbConnection.connect() != null) {
            System.out.println("Kết nối cơ sở dữ liệu thành công.");
            // Khởi chạy ứng dụng JavaFX
            launch(args);
        } else {
            System.err.println("Kết nối cơ sở dữ liệu thất bại. Ứng dụng sẽ không khởi chạy.");
            System.exit(1); // Thoát ứng dụng nếu kết nối thất bại
        }
    }

    // Phương thức tiện ích để lấy kết nối cơ sở dữ liệu trong toàn ứng dụng
    public static DatabaseConnectionModel getDatabaseConnection() {
        return dbConnection;
    }
}
