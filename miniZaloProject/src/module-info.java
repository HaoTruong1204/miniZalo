module miniZaloProject {
    requires javafx.controls; // Cần thiết cho các thành phần JavaFX cơ bản
    requires javafx.fxml;     // Cần thiết cho JavaFX FXML
    requires java.sql;       // Cần thiết nếu bạn sử dụng kết nối cơ sở dữ liệu

    // Xuất các gói để cho phép JavaFX và các thành phần khác truy cập vào
    exports app;          // Xuất gói chứa lớp App
    exports controller;   // Xuất gói chứa các controller cho JavaFX
    exports model;        // Xuất gói chứa các model cho ứng dụng
    exports network;      // Xuất gói chứa ClientConnection và các class liên quan

    // Mở các gói cho JavaFX để sử dụng với FXML
    opens controller to javafx.fxml; // Mở gói controller để JavaFX có thể truy cập
    opens model to javafx.fxml;      // Mở gói model để JavaFX có thể truy cập
    opens app to javafx.fxml;        // Mở gói app để JavaFX có thể truy cập vào lớp App
    
}
