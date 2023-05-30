package com.example.fighting;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Character {
    private Image image;
    private Position pos;

    protected double health;
    protected double maxHealth = 1000;
    protected double damage;

    protected double radiusRange = 100;

    public enum Status {
        NONE,
        FIRING
    }

    private boolean allowedToFire = false;
    Status status = Status.NONE;

    public void setAbleToFire(boolean ableToFire) {
        this.allowedToFire = ableToFire;
    }

    public boolean isAbleToFire() {
        return allowedToFire;
    }

    public Character(Image image, Position pos) {
        this.image = image;
        this.pos = pos;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.strokeRect(pos.getX(), pos.getY()-15, image.getWidth(), 10);
        gc.fillRect(pos.getX(), pos.getY()-15, image.getWidth() * health / maxHealth, 10);
        gc.drawImage(image, pos.getX(), pos.getY());
        if (status == Status.FIRING) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(getCenterPos().getX() - radiusRange, getCenterPos().getY() - radiusRange, 2 * radiusRange, 2 * radiusRange);

            double fireSpeed = 5;

            fireLineStartPos.translate(fireLineEndPos, fireSpeed);

            gc.fillOval(fireLineStartPos.getX(), fireLineStartPos.getY(), 5, 5);

            if (fireLineStartPos.equals(fireLineEndPos)) {
                fireLineEndPos = null;
                fireLineStartPos = null;
                status = Status.NONE;
            }
        }
    }

    Position fireLineStartPos = null;
    Position fireLineEndPos = null;

    public void fire(Character opponent) {
        fireLineEndPos = opponent.getCenterPos().clone();
        fireLineStartPos = getCenterPos().clone();
        status = Status.FIRING;
    }

    public void move(Canvas canvas, Position.Direction direction, double delta) {
        Position clonePos = this.pos.clone();
        clonePos.translate(direction, delta);
        boolean validTranslate = true;
        switch (direction) {
            case LEFT -> {
                if (clonePos.getX() < 0) validTranslate = false;
                break;
            }
            case RIGHT -> {
                if (clonePos.getX() + image.getWidth() > canvas.getWidth()) validTranslate = false;
                break;
            }
            case UP -> {
                if (clonePos.getY() < 0) validTranslate = false;
                break;
            }
            case DOWN -> {
                if (clonePos.getY() + image.getHeight() > canvas.getHeight()) validTranslate = false;
                break;
            }
        }
        if (validTranslate)
            pos.translate(direction, delta);
    }

    Position getTopLeftPos() {
        return pos;
    }

    Position getTopRightPos() {
        return pos.clone().translate(Position.Direction.RIGHT, image.getWidth());
    }

    Position getBottomLeftPos() {
        return pos.clone().translate(Position.Direction.DOWN, image.getHeight());
    }

    Position getBottomRightPos() {
        return getTopRightPos().translate(Position.Direction.DOWN, image.getHeight());
    }

    Position getCenterPos() {
        return pos.clone().translate(Position.Direction.DOWN, image.getHeight() / 2)
                .translate(Position.Direction.RIGHT, image.getWidth() / 2);
    }

    public boolean insideRange(Position position) {
        return radiusRange > Position.distance(getCenterPos(), position);
    }

    public boolean insideRange(Character character) {
        boolean inside = false;
        List<Position> posList = new ArrayList<>(Arrays.asList(character.getTopLeftPos(),
                character.getBottomLeftPos(), character.getBottomRightPos(), character.getTopRightPos()));
        for (Position position : posList) {
            if (radiusRange > Position.distance(getCenterPos(), position)) {
                inside = true;
                break;
            }
        }
        return inside;
    }
}
