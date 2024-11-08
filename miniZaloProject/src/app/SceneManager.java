package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private final Stack<Scene> sceneStack = new Stack<>(); // Stack để lưu các scene trước đó

    // Constructor private để đảm bảo Singleton
    private SceneManager() {}

    // Phương thức để lấy instance duy nhất của SceneManager (Singleton)
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // Khởi tạo SceneManager với primaryStage từ App class
    public void initialize(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        } else {
            throw new IllegalStateException("SceneManager đã được khởi tạo.");
        }
    }

    // Phương thức lấy primaryStage
    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager chưa được khởi tạo. Hãy gọi initialize() trước khi sử dụng.");
        }
        return primaryStage;
    }

    // Đẩy một scene mới từ FXML vào stack và chuyển đổi scene
    public void pushScene(String fxmlFilePath) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager chưa được khởi tạo. Hãy gọi initialize() trước khi sử dụng.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            // Kiểm tra nếu root bị null
            if (root == null) {
                throw new IOException("Không thể tải file FXML: " + fxmlFilePath + ". File FXML có thể bị thiếu hoặc đường dẫn không đúng.");
            }

            // Lưu scene hiện tại vào stack trước khi chuyển sang scene mới
            saveCurrentScene();

            // Tạo và thiết lập scene mới từ FXML
            setNewScene(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải file FXML: " + fxmlFilePath + ". Kiểm tra lại đường dẫn hoặc tên file.");
        }
    }

    // Phương thức để đẩy scene mới từ Parent
    public void pushScene(Parent root) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager chưa được khởi tạo. Hãy gọi initialize() trước khi sử dụng.");
        }

        if (root == null) {
            throw new IllegalArgumentException("Parent root không được null."); // Kiểm tra null cho root
        }

        // Lưu scene hiện tại vào stack trước khi chuyển sang scene mới
        saveCurrentScene();

        // Tạo và thiết lập scene mới từ Parent
        System.out.println("open: " + root.toString());
        setNewScene(root);
    }

    // Trở lại scene trước đó từ stack
    public void popScene() {
        if (!sceneStack.isEmpty()) {
            Scene previousScene = sceneStack.pop();
            primaryStage.setScene(previousScene);
            primaryStage.show();
        } else {
            System.out.println("Không có scene nào trong stack để quay lại.");
        }
    }

    // Lưu Scene hiện tại vào stack
    private void saveCurrentScene() {
        if (primaryStage.getScene() != null) {
            sceneStack.push(primaryStage.getScene());
        }
    }

    // Thiết lập Scene mới từ Parent và hiển thị
    private void setNewScene(Parent root) {
        Scene newScene = new Scene(root);
        primaryStage.setScene(newScene);
        primaryStage.show();
    }
}
