package com.survival.survivalgame.models;

/**
 * The base class for all objects in the game.
 * It holds common properties like position (x and y coordinates).
 */
public class GameObject {
    protected double x;
    protected double y;

    /**
     * Constructs a new GameObject with a specified position.
     * @param x The x-coordinate of the object.
     * @param y The y-coordinate of the object.
     */
    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
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
}
