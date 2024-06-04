module com.example.fx2048plus {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fx2048plus to javafx.fxml;
    exports com.example.fx2048plus;
    exports com.example.fx2048plus.controllers;
    opens com.example.fx2048plus.controllers to javafx.fxml;
    exports com.example.fx2048plus.config;
    opens com.example.fx2048plus.config to javafx.fxml;
    exports com.example.fx2048plus.game;
    opens com.example.fx2048plus.game to javafx.fxml;
}