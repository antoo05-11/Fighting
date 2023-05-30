package com.example.fighting;

public class Position implements Cloneable {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public enum Direction {
        LEFT,
        RIGHT,
        DOWN,
        UP;

    }

    public Position clone() {
        try {
            Position clone = (Position) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return new Position(x, y);
    }

    public static Direction getDirection(int enumValue) {
        switch (enumValue) {
            case 0 -> {
                return Direction.LEFT;
            }
            case 1 -> {
                return Direction.RIGHT;
            }
            case 2 -> {
                return Direction.DOWN;
            }
            case 3 -> {
                return Direction.UP;
            }
        }
        return null;
    }

    public static double distance(Position pos1, Position pos2) {
        double deltaX = pos1.x - pos2.x;
        double deltaY = pos1.y - pos2.y;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public Position translate(Position.Direction direction, double delta) {
        switch (direction) {
            case LEFT -> {
                x -= delta;
                break;
            }
            case RIGHT -> {
                x += delta;
                break;
            }
            case UP -> {
                y -= delta;
                break;
            }
            case DOWN -> {
                y += delta;
                break;
            }
        }
        return this;
    }

    public void translate(Position endPos, double fireSpeed) {
        if (Math.abs(endPos.x - x) < fireSpeed) x = endPos.x;
        if (x == endPos.x) {
            if (y > endPos.y) {
                this.translate(Direction.UP, fireSpeed);
            } else if (y < endPos.y) {
                this.translate(Direction.DOWN, fireSpeed);
            }
        } else {
            double oldX = x;
            if (x < endPos.x) {
                this.translate(Direction.RIGHT, fireSpeed);
            }
            if (x > endPos.x) {
                this.translate(Direction.LEFT, fireSpeed);
            }
            y = y + (endPos.y - y) * (x - oldX) / (endPos.x - oldX);
        }
        if (Math.abs(endPos.y - y) < fireSpeed) y = endPos.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position position) {
            return (position.x == x && position.y == y);
        }
        return false;
    }
}
