package com.survival.survivalgame.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single area in the game's grid. Each area contains its own set of enemies and items.
 */
public class Area {

    private int gridX;
    private int gridY;
    private List<Enemy> enemies;
    private List<Item> items;

    /**
     * Constructs an Area at a specific grid coordinate.
     * @param gridX The column index of the area in the grid.
     * @param gridY The row index of the area in the grid.
     */
    public Area(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    /**
     * Adds an enemy to this area.
     * @param enemy The enemy to add.
     */
    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    /**
     * Adds an item to this area.
     * @param item The item to add.
     */
    public void addItem(Item item) {
        this.items.add(item);
    }

    // Getters
    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Item> getItems() {
        return items;
    }
}
