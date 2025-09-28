module com.survival.survivalgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.survival.survivalgame to javafx.fxml;
    exports com.survival.survivalgame.controllers to javafx.fxml;
    opens com.survival.survivalgame.controllers to javafx.fxml;
    exports com.survival.survivalgame;
}