package com.example.fighting;

import javafx.scene.image.Image;

import java.util.Objects;

public class Jaian extends Character {

    public Jaian(Position pos) {
        super(new Image(Objects.requireNonNull(HelloApplication.class.getResource("jaian_icon.png")).toExternalForm()), pos);
        health = 1000;
        damage = 100;
    }
}
