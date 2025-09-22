package com.survival.survivalgame.models;

/**
 * Represents a First Aid Kit, a type of item that restores the player's health.
 */
public class FirstAidKit extends Item {

    private final double healingAmount;

    /**
     * Constructs a FirstAidKit at a given position.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param healingAmount The amount of health to restore.
     */
    public FirstAidKit(double x, double y, double healingAmount) {
        super(x, y);
        this.healingAmount = healingAmount;
    }

    /**
     * Applies the healing effect to the player by increasing their health.
     * @param player The player receiving the effect.
     */
    @Override
    public void applyEffect(Player player) {
        player.setHealth(player.getHealth() + healingAmount);
        if (player.getHealth() > 100) {
            player.setHealth(100);
        }
    }
}
