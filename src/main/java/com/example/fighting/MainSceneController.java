package com.example.fighting;

import com.example.fighting.character.Character;
import com.example.fighting.character.CharacterConfiguration;
import com.example.fighting.character.Jaian;
import com.example.fighting.character.Nobita;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainSceneController implements Initializable {
    @FXML
    private ScrollPane mainView;
    @FXML
    private ScrollPane boxChatScrollPane;
    @FXML
    private Button playButton;
    @FXML
    private Label myIDLabel;
    @FXML
    private Label opponentIDLabel;
    @FXML
    private Label loadingLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private Label opponentUsernameLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField chatTextField;
    @FXML
    private VBox chatBox;
    @FXML
    private Button sendMessageButton;
    private int userID;
    private int opponentID = -1;
    Canvas canvas;
    private SQLConnection sqlConnection;
    private String hashDeviceID;

    private final List<Character> characterList = new ArrayList<>();
    private long previousTime = 0;

    public void setHashDeviceID(String hashDeviceID) {
        this.hashDeviceID = hashDeviceID;
    }

    public void setSQLConnection(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public Label getLoadingLabel() {
        return loadingLabel;
    }

    public void setUserID(int userID) {
        this.userID = userID;
        Platform.runLater(() -> myIDLabel.setText(String.valueOf(userID)));
    }

    void displayMessage(String msg) {
        List<Label> displayMsg = new ArrayList<>();
        Label msgLabel = new Label();
        int index = 0;
        StringBuilder lineMsg = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            lineMsg.append(msg.charAt(i));
            Text msgText = new Text(lineMsg.toString());
            msgText.setFont(msgLabel.getFont());
            if (index == msg.length() - 1 || msgText.getBoundsInLocal().getWidth() >= chatBox.getWidth() - 25) {
                displayMsg.add(new Label(lineMsg.toString()));
                lineMsg.delete(0, lineMsg.length() - 1);
            }
            index++;
        }
        Platform.runLater(() -> {
            for (Label label : displayMsg) {
                chatBox.getChildren().add(label);
            }
            boxChatScrollPane.layout();
            boxChatScrollPane.setVvalue(boxChatScrollPane.getHmax());
            chatTextField.setText("");
        });

    }

    Character opponent;
    Character player;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        canvas = new Canvas(500, 500);
        mainView.setContent(canvas);
        chatBox.setPrefHeight(boxChatScrollPane.getWidth());
        sendMessageButton.setDisable(true);
        playButton.setOnMouseClicked(event -> {
            playButton.setDisable(true);
            String query = String.format("update users set status = 'find_opponent', username = '%s' where deviceID = '%s';", usernameTextField.getText(), hashDeviceID);
            runTask(() -> sqlConnection.updateQuery(query), () -> runTask(this::waitForMatching, () -> {
                System.out.println("finding successfully!");
            }, loadingLabel, null), loadingLabel, null);
        });

        sendMessageButton.setOnAction(actionEvent -> {
            if (!Objects.equals(chatTextField.getText(), "") && !sendMessageButton.isDisable()) {
                String msg = chatTextField.getText();
                displayMessage("You: " + msg);
                runTask(() -> {
                    String query = String.format("update chats set message = '%s' where matchID = %d and userID = %d", msg, matchID, userID);
                    sqlConnection.updateQuery(query);
                }, null, null, null);
                chatTextField.setText("");
            }
        });

        chatTextField.setOnKeyPressed(even -> {
            if (even.getCode() == KeyCode.ENTER) {
                sendMessageButton.fire();
            }
        });
    }

    int matchID = -1;
    Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    void initialSetup() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        ((StackPane) scene.getRoot()).setMinWidth(50);

        if (playerConfiguration.getCharacterID() == 1)
            player = new Jaian(playerConfiguration.getInitialPos());
        else player = new Nobita(playerConfiguration.getInitialPos());

        if (opponentConfiguration.getCharacterID() == 1)
            opponent = new Jaian(opponentConfiguration.getInitialPos());
        else opponent = new Nobita(opponentConfiguration.getInitialPos());

        characterList.add(player);
        characterList.add(opponent);

        double deltaMove = 4;
        Timeline fireBreakTime1 = new Timeline(new KeyFrame(new Duration(200), actionEvent -> {
        }));

        Set<Position.Direction> keysPressedByPlayer = new HashSet<>();
        mainView.setDisable(false);
        mainView.setFocusTraversable(true);
        mainView.setOnKeyReleased(keyEvent -> {
            Thread thread = new Thread(() -> {
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    keysPressedByPlayer.remove(Position.Direction.LEFT);
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    keysPressedByPlayer.remove(Position.Direction.RIGHT);
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    keysPressedByPlayer.remove(Position.Direction.DOWN);
                } else if (keyEvent.getCode() == KeyCode.UP) {
                    keysPressedByPlayer.remove(Position.Direction.UP);
                }
            });
            thread.start();
        });
        mainView.setOnKeyPressed(keyEvent -> {
            Thread thread1 = new Thread(() -> {
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    keysPressedByPlayer.add(Position.Direction.LEFT);
                }
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    keysPressedByPlayer.add(Position.Direction.RIGHT);
                }
                if (keyEvent.getCode() == KeyCode.DOWN) {
                    keysPressedByPlayer.add(Position.Direction.DOWN);
                }
                if (keyEvent.getCode() == KeyCode.UP) {
                    keysPressedByPlayer.add(Position.Direction.UP);
                }
                if (keyEvent.getCode() == KeyCode.P) {
                    if (fireBreakTime1.getStatus() != Animation.Status.RUNNING) {
                        fireBreakTime1.play();
                        if (player.insideRange(opponent)) {
                            opponent.health -= player.damage;
                            player.fire(opponent);
                        }
                    }
                }
            });
            thread1.start();
        });

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                double elapsedTime = (currentTime - previousTime) / 1_000_000_000.0;
                if (elapsedTime >= 1.0 / 60.0) {
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    for (Position.Direction direction : keysPressedByPlayer) {
                        player.move(canvas, direction, deltaMove);
                        runTask(() -> {
                            String query = String.format("update matchDetails set xPos = '%s', yPos = '%s' where matchID = %d and userID = %d",
                                    String.valueOf(player.getPos().getX()).replace(',', '.'),
                                    String.valueOf(player.getPos().getY()).replace(',', '.'),
                                    matchID, userID);
                            sqlConnection.updateQuery(query);
                        }, null, null, null);
                    }
                    for (Character character : characterList) character.render(gc);
                    previousTime = currentTime;
                }
            }
        };
        animationTimer.start();
    }

    CharacterConfiguration playerConfiguration;
    CharacterConfiguration opponentConfiguration;

    void waitForMatching() {
        Platform.runLater(() -> loadingLabel.setText("Finding opponent..."));
        while (matchID == -1) {
            System.out.println("wait for matching...");
            String query = "select * from users where userID = " + userID;
            ResultSet resultSet = sqlConnection.getDataQuery(query);
            try {
                if (resultSet.next()) {
                    matchID = resultSet.getInt("matchID");
                    if (matchID > 0) {
                        query = "select * from matchDetails where matchID = " + matchID;
                        resultSet = sqlConnection.getDataQuery(query);
                        while (resultSet.next()) {
                            if (resultSet.getInt("userID") == userID) {
                                playerConfiguration = new CharacterConfiguration(new Position(resultSet.getDouble("xPos"), resultSet.getDouble("yPos")),
                                        resultSet.getInt("characterID"));
                            } else {
                                opponentConfiguration = new CharacterConfiguration(new Position(resultSet.getDouble("xPos"), resultSet.getDouble("yPos")),
                                        resultSet.getInt("characterID"));
                                opponentID = resultSet.getInt("userID");
                                Platform.runLater(() -> opponentIDLabel.setText(String.valueOf(opponentID)));
                            }
                        }

                        initialSetup();
                        startUpdatingOpponentInfo();
                        sendMessageButton.setDisable(false);
                        startGettingMessage();

                        //Get opponent's username.
                        query = "select * from users where userID = " + opponentID;
                        resultSet = sqlConnection.getDataQuery(query);
                        if (resultSet.next()) {
                            if (resultSet.getString("username") != null) {
                                String opponentName = resultSet.getString("username");
                                Platform.runLater(() -> opponentUsernameLabel.setText(opponentName));
                            }
                        }

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    BooleanProperty playTurn = new SimpleBooleanProperty(false);
    Timeline timeline = new Timeline(new KeyFrame(new Duration(3000), actionEvent -> {
        firstTurnID = -1;
        matchID = -1;
        playTurn.setValue(false);
        Platform.runLater(() -> {
            resultLabel.setText("PLAYING");
            opponentUsernameLabel.setText("");
            myIDLabel.setText("");
            opponentIDLabel.setText("");
            loadingLabel.setText("");
        });

        playButton.setDisable(false);
    }));
    int firstTurnID;
    RESULT result;


    static public void runTask(Runnable taskFunction, Runnable finishFunction, Node progressIndicator, Node bannedArea) {
        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() {
                taskFunction.run();
                return null;
            }
        };
        if (progressIndicator != null) {
            progressIndicator.visibleProperty().bind(task.runningProperty());
        }
        if (bannedArea != null) {
            bannedArea.disableProperty().bind(task.runningProperty());
        }
        if (finishFunction != null) {
            task.setOnSucceeded(workerStateEvent -> finishFunction.run());
        }
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    enum RESULT {
        VICTORY, DEFEAT, NONE
    }

    @FXML
    Label onlineNumberLabel;
    @FXML
    Label readyNumberLabel;
    String opponentMessage = "";

    void startGettingMessage() {
        runTask(() -> {
            if (matchID == -1) {
                chatBox.getChildren().clear();
            }
            while (matchID != -1) {
                String query = String.format("select message from chats where matchID = %d and userID = %d", matchID, opponentID);
                ResultSet resultSet = sqlConnection.getDataQuery(query);
                try {
                    if (resultSet.next()) {
                        if (!opponentMessage.equals(resultSet.getString("message"))) {
                            String msg = resultSet.getString("message");
                            if (opponentUsernameLabel.getText().equals("")) msg = opponentID + ": " + msg;
                            else msg = opponentUsernameLabel.getText() + ": " + msg;
                            displayMessage(msg);
                            opponentMessage = resultSet.getString(1);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, null, null, null);
    }

    void startUpdatingOpponentInfo() {
        runTask(() -> {
            while (matchID != -1) {
                String query = String.format("select * from matchDetails where matchID = %d and userID = %d", matchID, opponentID);
                try {
                    ResultSet resultSet = sqlConnection.getDataQuery(query);
                    while (resultSet.next()) {
                        double opponentX = resultSet.getDouble("xPos");
                        double opponentY = resultSet.getDouble("YPos");
                        if (opponentX != -1 && opponentY != -1)
                            opponent.move(canvas, new Position(opponentX, opponentY));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, null, null, null);

    }

    void keepConnection() {
        while (true) {
            String query = "select * from " +
                    "    (select count(*) from users where status != 'offline') as t1 " +
                    "cross join (select count(*) from users where status = 'find_opponent') as t2;";
            ResultSet resultSet = sqlConnection.getDataQuery(query);
            try {
                if (resultSet.next()) {
                    int onlineNumber = resultSet.getInt(1);
                    int readyNumber = resultSet.getInt(2);
                    Platform.runLater(() -> {
                        onlineNumberLabel.setText("Online: " + onlineNumber);
                        readyNumberLabel.setText("Ready to play: " + readyNumber);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            query = "select * from users where userID = " + userID;
            resultSet = sqlConnection.getDataQuery(query);
            try {
                if (resultSet.next()) {
                    String connectionMessage = resultSet.getString("connectionMessage");
                    if (connectionMessage != null && !connectionMessage.matches("\\d")) {
                        String[] nums = connectionMessage.split("\\+");
                        if (nums.length != 1) {
                            connectionMessage = String.valueOf(Integer.parseInt(nums[0]) + Integer.parseInt(nums[1]));
                            query = String.format("update users set connectionMessage = '%s' where userID = %d;",
                                    connectionMessage, userID);
                            sqlConnection.updateQuery(query);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}