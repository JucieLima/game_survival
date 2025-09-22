package com.survival.survivalgame.models;

/**
 * Represents a bullet projectile fired by the player.
 */
public class Bullet extends GameObject {

    private final Direction direction;
    private final double speed;
    private final double damage;
    private final double radius = 5; // Define o raio do projétil
    private boolean isActive;

    /**
     * Constructs a new Bullet object.
     * @param x The initial x-coordinate of the bullet.
     * @param y The initial y-coordinate of the bullet.
     * @param direction The direction vector for the bullet's movement.
     * @param speed The speed of the bullet.
     * @param damage The damage the bullet inflicts on enemies.
     */
    public Bullet(double x, double y, Direction direction, double speed, double damage) {
        super(x, y);
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        this.isActive = true;
    }

    /**
     * Updates the bullet's position based on its direction and speed.
     */
    public void update() {
        this.x += direction.getDx() * speed;
        this.y += direction.getDy() * speed;
    }

    // Getters
    public double getDamage() {
        return damage;
    }

    /**
     * Gets the radius of the bullet for collision detection.
     * @return The bullet's radius.
     */
    public double getRadius() {
        return radius;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the bullet to an inactive state.
     */
    public void deactivate() {
        this.isActive = false;
    }
}
