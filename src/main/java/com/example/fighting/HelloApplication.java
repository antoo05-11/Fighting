package com.example.fighting;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HelloApplication extends Application {
    private final List<Character> characterList = new ArrayList<>();
    private long previousTime = 0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
       // stage.initStyle(StageStyle.TRANSPARENT);
        //ResizeHelper.addResizeListener(stage);
        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        scene.setRoot(new StackPane(canvas));
        ((StackPane)scene.getRoot()).setMinWidth(50);

        Character player1 = new Nobita(new Position(0, 200));
        characterList.add(player1);

        Character player2 = new Jaian(new Position(430, 200));
        characterList.add(player2);

        double deltaMove = 4;
        Timeline fireBreakTime1 = new Timeline(new KeyFrame(new Duration(200), actionEvent -> {
        }));
        Timeline fireBreakTime2 = new Timeline(new KeyFrame(new Duration(200), actionEvent -> {
        }));
        Set<Position.Direction> keysPressedByPlayer1 = new HashSet<>();
        Set<Position.Direction> keysPressedByPlayer2 = new HashSet<>();
        scene.setOnKeyReleased(keyEvent -> {
            Thread thread = new Thread(() -> {
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    keysPressedByPlayer1.remove(Position.Direction.LEFT);
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    keysPressedByPlayer1.remove(Position.Direction.RIGHT);
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    keysPressedByPlayer1.remove(Position.Direction.DOWN);
                } else if (keyEvent.getCode() == KeyCode.UP) {
                    keysPressedByPlayer1.remove(Position.Direction.UP);
                }

                if (keyEvent.getCode() == KeyCode.A) {
                    keysPressedByPlayer2.remove(Position.Direction.LEFT);
                } else if (keyEvent.getCode() == KeyCode.D) {
                    keysPressedByPlayer2.remove(Position.Direction.RIGHT);
                } else if (keyEvent.getCode() == KeyCode.S) {
                    keysPressedByPlayer2.remove(Position.Direction.DOWN);
                } else if (keyEvent.getCode() == KeyCode.W) {
                    keysPressedByPlayer2.remove(Position.Direction.UP);
                }
            });
            thread.start();
        });
        scene.setOnKeyPressed(keyEvent -> {
            Thread thread1 = new Thread(() -> {
                // For player 1's input.
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    keysPressedByPlayer1.add(Position.Direction.LEFT);
                }
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    keysPressedByPlayer1.add(Position.Direction.RIGHT);
                }
                if (keyEvent.getCode() == KeyCode.DOWN) {
                    keysPressedByPlayer1.add(Position.Direction.DOWN);
                }
                if (keyEvent.getCode() == KeyCode.UP) {
                    keysPressedByPlayer1.add(Position.Direction.UP);
                }
                if (keyEvent.getCode() == KeyCode.P) {
                    if (fireBreakTime1.getStatus() != Animation.Status.RUNNING) {
                        fireBreakTime1.play();
                        if (player1.insideRange(player2)) {
                            player2.health -= player1.damage;
                            player1.fire(player2);
                        }
                    }
                }
            });
            thread1.start();
            Thread thread2 = new Thread(() -> {
                // For player 2 's input.
                if (keyEvent.getCode() == KeyCode.A) {
                    keysPressedByPlayer2.add(Position.Direction.LEFT);
                }
                if (keyEvent.getCode() == KeyCode.D) {
                    keysPressedByPlayer2.add(Position.Direction.RIGHT);
                }
                if (keyEvent.getCode() == KeyCode.S) {
                    keysPressedByPlayer2.add(Position.Direction.DOWN);
                }
                if (keyEvent.getCode() == KeyCode.W) {
                    keysPressedByPlayer2.add(Position.Direction.UP);
                }
                if (keyEvent.getCode() == KeyCode.F) {
                    if (fireBreakTime2.getStatus() != Animation.Status.RUNNING) {
                        fireBreakTime2.play();
                        if (player2.insideRange(player1)) {
                            player1.health -= player2.damage;
                            player2.fire(player1);
                        }
                    }
                }
            });
            thread2.start();
        });

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                double elapsedTime = (currentTime - previousTime) / 1_000_000_000.0;
                if (elapsedTime >= 1.0 / 60.0) {
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    for (Position.Direction direction : keysPressedByPlayer1) {
                        player1.move(canvas, direction, deltaMove);
                    }
                    for (Position.Direction direction : keysPressedByPlayer2) {
                        player2.move(canvas, direction, deltaMove);
                    }
                    for (Character character : characterList) character.render(gc);
                    previousTime = currentTime;
                }
            }
        };
        animationTimer.start();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
