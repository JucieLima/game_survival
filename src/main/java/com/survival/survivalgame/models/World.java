package com.survival.survivalgame.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the game world, which is composed of a grid of Areas.
 * It tracks which areas are "active" (i.e., near the player) and provides
 * methods for accessing and managing these areas.
 */
public class World {
    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;
    private static final double AREA_WIDTH = 300;
    private static final double AREA_HEIGHT = 200;

    private final Area[][] areas;
    private final Player player;

    public World(Player player) {
        this.player = player;
        this.areas = new Area[GRID_WIDTH][GRID_HEIGHT];
        // Initialize the grid of areas
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                areas[i][j] = new Area(i, j);
            }
        }
    }

    /**
     * Gets the area at the given world coordinates.
     * @param worldX The x-coordinate in the world.
     * @param worldY The y-coordinate in the world.
     * @return The Area object at the specified coordinates, or null if coordinates are out of bounds.
     */
    public Area getAreaAt(double worldX, double worldY) {
        int gridX = (int) Math.floor(worldX / AREA_WIDTH);
        int gridY = (int) Math.floor(worldY / AREA_HEIGHT);

        // Clamp coordinates to be within the grid bounds
        gridX = Math.max(0, Math.min(gridX, GRID_WIDTH - 1));
        gridY = Math.max(0, Math.min(gridY, GRID_HEIGHT - 1));

        if (gridX >= 0 && gridX < GRID_WIDTH && gridY >= 0 && gridY < GRID_HEIGHT) {
            return areas[gridX][gridY];
        }
        return null; // Should not happen with clamping, but good practice
    }

    /**
     * Returns a list of all areas in the world.
     * @return A list of all areas.
     */
    public List<Area> getAllAreas() {
        List<Area> allAreas = new ArrayList<>();
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                allAreas.add(areas[i][j]);
            }
        }
        return allAreas;
    }

    /**
     * Returns a list of areas that are currently active (the player's current area and its neighbors).
     * @return A list of active areas.
     */
    public List<Area> getActiveAreas() {
        List<Area> activeAreas = new ArrayList<>();
        Area currentArea = getAreaAt(player.getX(), player.getY());
        if (currentArea == null) {
            return activeAreas; // Return an empty list if player is outside the grid
        }

        int playerGridX = (int) (player.getX() / AREA_WIDTH);
        int playerGridY = (int) (player.getY() / AREA_HEIGHT);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int neighborX = playerGridX + i;
                int neighborY = playerGridY + j;

                if (neighborX >= 0 && neighborX < GRID_WIDTH && neighborY >= 0 && neighborY < GRID_HEIGHT) {
                    activeAreas.add(areas[neighborX][neighborY]);
                }
            }
        }
        return activeAreas;
    }

    // Getters for world dimensions
    public double getGridWidth() {
        return GRID_WIDTH;
    }

    public double getGridHeight() {
        return GRID_HEIGHT;
    }

    public double getAreaWidth() {
        return AREA_WIDTH;
    }

    public double getAreaHeight() {
        return AREA_HEIGHT;
    }
}
