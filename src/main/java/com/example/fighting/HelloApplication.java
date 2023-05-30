package com.example.fighting;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.example.fighting.MainSceneController.runTask;

public class HelloApplication extends Application {

    static SQLConnection sqlConnection;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainSceneController controller = fxmlLoader.getController();
        stage.setTitle("Fighting");
        stage.setOnCloseRequest(windowEvent -> System.exit(0));

        stage.getIcons().add(new Image(this.getClass().getResource("stageIcon.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();

        sqlConnection = new SQLConnection();
        InputStream fstream = this.getClass().getResourceAsStream("config.txt");
        sqlConnection.configure(fstream, "localhost");

        controller.setSQLConnection(sqlConnection);
        controller.setScene(scene);
        runTask(() -> {
            sqlConnection.connectServer();
            Platform.runLater(() -> {
                controller.getLoadingLabel().setText("Connecting server...");
            });
            String hashDeviceID = LocalDateTime.now().toString() + Math.random() * 100000000;
            String query = String.format("insert into users (deviceID) VALUES ('%s');", hashDeviceID);
            controller.setHashDeviceID(hashDeviceID);
            sqlConnection.updateQuery(query);
            query = String.format("select userID from users where deviceID = '%s';", hashDeviceID);
            ResultSet resultSet = sqlConnection.getDataQuery(query);
            try {
                if (resultSet.next()) controller.setUserID(resultSet.getInt("userID"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, () -> runTask(controller::keepConnection, null, null, null), controller.getLoadingLabel(), null);


        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
