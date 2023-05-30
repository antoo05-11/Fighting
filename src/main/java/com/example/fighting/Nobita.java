package com.example.fighting;

import javafx.scene.image.Image;

import java.util.Objects;

public class Nobita extends Character{
    public Nobita(Position pos) {
        super(new Image(Objects.requireNonNull(HelloApplication.class.getResource("nobita_icon.png")).toExternalForm()), pos);
        health = 1000;
        damage = 50;
    }
}
