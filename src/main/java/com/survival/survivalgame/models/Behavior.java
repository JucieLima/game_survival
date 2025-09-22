package com.survival.survivalgame.models;

/**
 * An abstract class defining the behavior interface for enemies.
 * Concrete behavior classes must implement the 'update' method.
 */
public abstract class Behavior {
    /**
     * Updates the enemy's state based on its specific behavior.
     * @param enemy The enemy object to update.
     * @param player The player object, required for behaviors that interact with the player.
     */
    public abstract void update(Enemy enemy, Player player);
}
