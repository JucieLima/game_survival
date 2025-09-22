package com.survival.survivalgame.models;

/**
 * Represents a 2D direction vector with horizontal (dx) and vertical (dy) components.
 * This class is used to abstract player movement input.
 */
public class Direction {

    private final double dx;
    private final double dy;

    /**
     * Constructs a new Direction object.
     * @param dx The horizontal component of the direction vector.
     * @param dy The vertical component of the direction vector.
     */
    public Direction(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Getters
    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }
}
