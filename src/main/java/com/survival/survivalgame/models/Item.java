package com.survival.survivalgame.models;

/**
 * Represents a collectible item in the game.
 * This is a base class that can be extended by specific item types.
 */
public abstract class Item {
    protected double x;
    protected double y;

    /**
     * Constructs an item at a given position.
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     */
    public Item(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Applies the effect of the item to the player.
     * This method must be implemented by subclasses.
     * @param player The player receiving the effect.
     */
    public abstract void applyEffect(Player player);

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
