module com.example.fighting {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;

    opens com.example.fighting to javafx.fxml;
    exports com.example.fighting;
    exports com.example.fighting.character;
    opens com.example.fighting.character to javafx.fxml;
}