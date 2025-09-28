package com.survival.survivalgame.core;

import com.survival.survivalgame.models.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Responsible for rendering the minimap, showing the entire 3x3 grid with player, enemies, and items.
 */
public class MinimapRenderer {
    private final GraphicsContext gc;
    private final World world;
    private final Canvas gameCanvas;

    private static final double MINIMAP_SCALE = 0.1;
    private static final double MINIMAP_WIDTH = 270; // Reflects 2700 units (3x900)
    private static final double MINIMAP_HEIGHT = 180; // Reflects 1800 units (3x600)
    private static final double MINIMAP_X = 10;

    public MinimapRenderer(GraphicsContext gc, World world, Canvas gameCanvas) {
        this.gc = gc;
        this.world = world;
        this.gameCanvas = gameCanvas;
    }

    public void render(Player player) {
        // Save the current graphics context state
        gc.save();

        // Set minimap position and size (bottom-left corner)
        double dynamicMinimapY = gameCanvas.getHeight() - MINIMAP_HEIGHT - 10;
        gc.translate(MINIMAP_X, dynamicMinimapY);
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT);

        // Draw grid borders based on 3x3 areas (900x600 each)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        for (int x = 0; x <= 3; x++) {
            gc.strokeLine(x * 900 * MINIMAP_SCALE, 0,
                    x * 900 * MINIMAP_SCALE, MINIMAP_HEIGHT);
        }
        for (int y = 0; y <= 3; y++) {
            gc.strokeLine(0, y * 600 * MINIMAP_SCALE,
                    MINIMAP_WIDTH, y * 600 * MINIMAP_SCALE);
        }

        // Draw player
        double playerMapX = player.getX() * MINIMAP_SCALE;
        double playerMapY = player.getY() * MINIMAP_SCALE;
        gc.setFill(Color.BLUE);
        gc.fillOval(playerMapX - 2, playerMapY - 2, 4, 4);

        // Draw enemies from all areas
        for (Area area : world.getAllAreas()) {
            for (Enemy enemy : area.getEnemies()) {
                double enemyMapX = enemy.getX() * MINIMAP_SCALE;
                double enemyMapY = enemy.getY() * MINIMAP_SCALE;
                gc.setFill(Color.RED);
                gc.fillOval(enemyMapX - 2, enemyMapY - 2, 4, 4);
            }
        }

        // Draw items from all areas
        for (Area area : world.getAllAreas()) {
            for (Item item : area.getItems()) {
                double itemMapX = item.getX() * MINIMAP_SCALE;
                double itemMapY = item.getY() * MINIMAP_SCALE;
                if (item instanceof FirstAidKit) {
                    gc.setFill(Color.GREEN);
                } else if (item instanceof AmmoBox) {
                    gc.setFill(Color.ORANGE);
                }
                gc.fillOval(itemMapX - 2, itemMapY - 2, 4, 4);
            }
        }

        // Restore the graphics context state
        gc.restore();
    }
}