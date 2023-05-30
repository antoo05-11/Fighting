module com.example.fighting {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.fighting to javafx.fxml;
    exports com.example.fighting;
}