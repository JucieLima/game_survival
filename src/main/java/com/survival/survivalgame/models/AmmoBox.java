package com.survival.survivalgame.models;

/**
 * Represents an Ammo Box, a type of item that restores the player's ammunition.
 */
public class AmmoBox extends Item {

    private final int ammoAmount;

    /**
     * Constructs an AmmoBox at a given position.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param ammoAmount The amount of ammo to restore.
     */
    public AmmoBox(double x, double y, int ammoAmount) {
        super(x, y);
        this.ammoAmount = ammoAmount;
    }

    /**
     * Applies the ammo replenishment effect to the player by increasing their ammo count.
     * @param player The player receiving the effect.
     */
    @Override
    public void applyEffect(Player player) {
        player.setAmmo(player.getAmmo() + ammoAmount);
    }
}
