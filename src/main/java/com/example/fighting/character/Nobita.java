package com.example.fighting.character;

import com.example.fighting.HelloApplication;
import com.example.fighting.Position;
import javafx.scene.image.Image;

import java.util.Objects;

public class Nobita extends Character {
    public Nobita() {

    }
    public Nobita(Position pos) {
        super(new Image(Objects.requireNonNull(HelloApplication.class.getResource("nobita_icon.png")).toExternalForm()), pos);
        health = 1000;
        damage = 50;
    }

    public void configure(Position pos) {
        super.configure(new Image(Objects.requireNonNull(HelloApplication.class.getResource("nobita_icon.png")).toExternalForm()), pos);
    }
}
