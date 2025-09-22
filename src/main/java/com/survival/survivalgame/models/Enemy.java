package com.survival.survivalgame.models;

/**
 * Represents an enemy in the game.
 */
public class Enemy extends GameObject {

    private double health;
    private final double damage;
    private final double radius = 15;
    private final Behavior behavior;
    private final Area area;

    /**
     * Constructs an Enemy with a specific position, health, damage, and behavior.
     * @param x The initial x-coordinate.
     * @param y The initial y-coordinate.
     * @param health The initial health of the enemy.
     * @param behavior The behavior pattern of the enemy.
     * @param area The area the enemy belongs to.
     */
    public Enemy(double x, double y, double health, Behavior behavior, Area area) {
        super(x, y);
        this.health = health;
        this.damage = 5.0; // Fixed damage for all enemies
        this.behavior = behavior;
        this.area = area;
    }

    /**
     * Updates the enemy's position based on its behavior.
     * @param player The player, needed for behaviors like FollowPlayerBehavior.
     */
    public void update(Player player) {
        behavior.update(this, player);
    }

    /**
     * Reduces the enemy's health by a specified amount.
     * @param damage The amount of damage to be taken.
     */
    public void takeDamage(double damage) {
        this.health -= damage;
    }

    /**
     * Checks if the enemy is still alive.
     * @return true if health is greater than 0, false otherwise.
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Gets the parent Area of this enemy.
     * @return The Area object to which the enemy belongs.
     */
    public Area getParentArea() {
        return area;
    }

    // Getters
    public double getHealth() {
        return health;
    }

    public double getDamage() {
        return damage;
    }

    public double getRadius() {
        return radius;
    }
}
