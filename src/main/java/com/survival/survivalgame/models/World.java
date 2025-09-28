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
    private static final double AREA_WIDTH = 900;
    private static final double AREA_HEIGHT = 600;

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
     * Returns a list of areas that are currently active based on player proximity to area borders.
     * An area is active if:
     * - It's the player's current area (always active), OR
     * - It's a neighboring area AND the player is within 40% of the shared border
     * @return A list of active areas.
     */
    public List<Area> getActiveAreas() {
        List<Area> activeAreas = new ArrayList<>();
        Area currentArea = getAreaAt(player.getX(), player.getY());
        if (currentArea == null) {
            return activeAreas;
        }

        // Sempre incluir a área atual
        activeAreas.add(currentArea);

        // Calcular posição relativa do player DENTRO da sua área atual
        int currentGridX = currentArea.getGridX();
        int currentGridY = currentArea.getGridY();

        double relativeX = player.getX() - (currentGridX * AREA_WIDTH);
        double relativeY = player.getY() - (currentGridY * AREA_HEIGHT);

        // Thresholds de proximidade às bordas (40% da área)
        double borderThresholdX = AREA_WIDTH * 0.4;  // 360 pixels
        double borderThresholdY = AREA_HEIGHT * 0.4; // 240 pixels

        // Verificar vizinhos baseado na proximidade às bordas
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // Pular a área atual (já adicionada)
                if (i == 0 && j == 0) {
                    continue;
                }

                int neighborX = currentGridX + i;
                int neighborY = currentGridY + j;

                // Verificar se vizinho está dentro dos limites do grid
                if (neighborX >= 0 && neighborX < GRID_WIDTH &&
                        neighborY >= 0 && neighborY < GRID_HEIGHT) {

                    Area neighborArea = areas[neighborX][neighborY];
                    boolean shouldActivate = false;

                    // Verificar se o player está próximo da borda compartilhada
                    if (i == 0 && j != 0) {
                        // Vizinho vertical (cima/baixo)
                        if (j == -1 && relativeY <= borderThresholdY) {
                            // Próximo da borda superior
                            shouldActivate = true;
                        } else if (j == 1 && relativeY >= AREA_HEIGHT - borderThresholdY) {
                            // Próximo da borda inferior
                            shouldActivate = true;
                        }
                    } else if (j == 0 && i != 0) {
                        // Vizinho horizontal (esquerda/direita)
                        if (i == -1 && relativeX <= borderThresholdX) {
                            // Próximo da borda esquerda
                            shouldActivate = true;
                        } else if (i == 1 && relativeX >= AREA_WIDTH - borderThresholdX) {
                            // Próximo da borda direita
                            shouldActivate = true;
                        }
                    } else if (i != 0 && j != 0) {
                        // Vizinho diagonal - verificar cantos
                        double cornerThreshold = 0.4; // 40% do menor lado para diagonal

                        if (i == -1 && j == -1) {
                            // Canto superior esquerdo
                            shouldActivate = (relativeX <= borderThresholdX) && (relativeY <= borderThresholdY);
                        } else if (i == 1 && j == -1) {
                            // Canto superior direito
                            shouldActivate = (relativeX >= AREA_WIDTH - borderThresholdX) && (relativeY <= borderThresholdY);
                        } else if (i == -1 && j == 1) {
                            // Canto inferior esquerdo
                            shouldActivate = (relativeX <= borderThresholdX) && (relativeY >= AREA_HEIGHT - borderThresholdY);
                        } else if (i == 1 && j == 1) {
                            // Canto inferior direito
                            shouldActivate = (relativeX >= AREA_WIDTH - borderThresholdX) && (relativeY >= AREA_HEIGHT - borderThresholdY);
                        }
                    }

                    if (shouldActivate) {
                        activeAreas.add(neighborArea);
                    }
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
