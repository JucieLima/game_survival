package com.survival.survivalgame.models;

/**
 * Represents the player character.
 */
public class Player extends GameObject {
    private double health;
    private double ammo;
    private World world;
    private static final double SPEED = 5.0; // Velocidade do jogador em pixels por frame
    private final double radius = 15;

    public Player(double x, double y, double health, double ammo) {
        super(x, y);
        this.health = health;
        this.ammo = ammo;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getAmmo() {
        return ammo;
    }

    public void setAmmo(double ammo) {
        this.ammo = ammo;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    /**
     * Gets the radius of the player for collision detection.
     * @return The player's radius.
     */
    public double getRadius() {
        return radius;
    }

    public void takeDamage(double damage) {
        this.health -= damage;
    }

    /**
     * Checks if the player is still alive.
     * @return true if health is greater than 0, false otherwise.
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Updates the player's position based on input and prevents it from moving out of bounds.
     * @param direction The direction vector for movement.
     * @param world The game world to check for boundaries.
     */
    public void update(Direction direction, World world) {
        if (direction.getDx() != 0 || direction.getDy() != 0) {
            double nextX = this.x + direction.getDx() * SPEED;
            double nextY = this.y + direction.getDy() * SPEED;

            // Boundary checks to keep the player within the world grid
            double minX = 0;
            double minY = 0;
            double maxX = world.getGridWidth() * world.getAreaWidth();
            double maxY = world.getGridHeight() * world.getAreaHeight();

            this.x = Math.max(minX, Math.min(nextX, maxX));
            this.y = Math.max(minY, Math.min(nextY, maxY));
        }
    }
}
