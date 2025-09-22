package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.AmmoBox;
import com.survival.survivalgame.models.Area;
import com.survival.survivalgame.models.Bullet;
import com.survival.survivalgame.models.Enemy;
import com.survival.survivalgame.models.FirstAidKit;
import com.survival.survivalgame.models.Item;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Manages all rendering operations, including drawing game objects and the HUD.
 */
public class GameRenderer {

    private final GameController gameController;
    private final GameUpdater gameUpdater;
    private final GraphicsContext gc;
    private final World world;
    private final Player player;
    private final Canvas gameCanvas;
    private final List<Bullet> bullets;

    public GameRenderer(GameController gameController, GameUpdater gameUpdater, GraphicsContext gc, World world, Player player, Canvas gameCanvas, List<Bullet> bullets) {
        this.gameController = gameController;
        this.gameUpdater = gameUpdater;
        this.gc = gc;
        this.world = world;
        this.player = player;
        this.gameCanvas = gameCanvas;
        this.bullets = bullets;
    }

    /**
     * The main rendering loop.
     */
    public void render() {
        // Draw a background fill to replace clearRect
        gc.setFill(Color.web("#333333"));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Save the current state of the graphics context
        gc.save();

        // Calculate the camera position (centered on the player)
        double cameraX = player.getX() - gameCanvas.getWidth() / 2;
        double cameraY = player.getY() - gameCanvas.getHeight() / 2;

        // Apply a translation to simulate the camera following the player
        gc.translate(-cameraX, -cameraY);

        // Draw the background areas of the world
        renderWorldAreas();

        // Draw items
        drawItems();

        // Draw bullets
        drawBullets();

        // Draw enemies
        drawEnemies();

        // Draw the player
        drawPlayer();

        // Restore the graphics context to its original state (before camera translation)
        gc.restore();

        // Draw the HUD (UI elements that stay fixed on screen)
        drawHUD();

        // Draw the end-of-game screen if applicable
        if (gameController.getGameState() != GameController.GameState.JOGANDO) {
            drawEndGameScreen();
        }
    }

    private void renderWorldAreas() {
        // Get active areas once to avoid redundant checks in the loop
        List<Area> activeAreas = world.getActiveAreas();
        Area currentArea = world.getAreaAt(player.getX(), player.getY());

        // Loop through all areas to render them
        for (Area area : world.getAllAreas()) {
            gc.setStroke(Color.web("#444444"));
            gc.setLineWidth(2);

            // Set the fill color based on whether the area is active or not
            if (area == currentArea) {
                gc.setFill(Color.LIGHTGRAY); // Current area
            } else if (activeAreas.contains(area)) {
                gc.setFill(Color.DARKGRAY); // Active neighbor area
            } else {
                gc.setFill(Color.web("#333333")); // Inactive area, matching the background
            }

            // Draw the filled rectangle for the area
            gc.fillRect(area.getGridX() * world.getAreaWidth(), area.getGridY() * world.getAreaHeight(), world.getAreaWidth(), world.getAreaHeight());

            // Draw the border rectangle
            gc.strokeRect(area.getGridX() * world.getAreaWidth(), area.getGridY() * world.getAreaHeight(), world.getAreaWidth(), world.getAreaHeight());
        }
    }

    private void drawPlayer() {
        gc.setFill(Color.BLUE);
        gc.fillOval(player.getX() - player.getRadius(), player.getY() - player.getRadius(), player.getRadius() * 2, player.getRadius() * 2);
    }

    private void drawEnemies() {
        for (Area area : world.getActiveAreas()) {
            for (Enemy enemy : area.getEnemies()) {
                gc.setFill(Color.RED);
                gc.fillOval(enemy.getX() - enemy.getRadius(), enemy.getY() - enemy.getRadius(), enemy.getRadius() * 2, enemy.getRadius() * 2);
            }
        }
    }

    private void drawBullets() {
        gc.setFill(Color.YELLOW);
        for (Bullet bullet : bullets) {
            gc.fillOval(bullet.getX() - bullet.getRadius(), bullet.getY() - bullet.getRadius(), bullet.getRadius() * 2, bullet.getRadius() * 2);
        }
    }

    private void drawItems() {
        for (Area area : world.getActiveAreas()) {
            for (Item item : area.getItems()) {
                if (item instanceof FirstAidKit) {
                    gc.setFill(Color.GREEN);
                    gc.fillOval(item.getX() - 10, item.getY() - 10, 20, 20);
                } else if (item instanceof AmmoBox) {
                    gc.setFill(Color.ORANGE);
                    gc.fillRect(item.getX() - 10, item.getY() - 10, 20, 20);
                }
            }
        }
    }

    private void drawHUD() {
        gc.setFill(Color.DARKRED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Player stats
        gc.setFill(Color.web("#F29F05"));
        gc.fillText("Saúde: " + (int) player.getHealth(), 10, 30);
        gc.fillText("Munição: " + (int) player.getAmmo(), 10, 60);
        gc.setFill(Color.RED);
        gc.fillRect(120, 10, 100, 20); // Background of the health bar
        gc.setFill(Color.GREEN);
        gc.fillRect(120, 10, player.getHealth(), 20);

        // Survival timer
        long seconds = gameUpdater.getSurvivalTimer() / 1000;
        gc.setFill(Color.web("#F29F05"));
        gc.fillText("Tempo: " + seconds, gameCanvas.getWidth() - 120, 30);
    }

    private void drawEndGameScreen() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.setTextAlign(TextAlignment.CENTER);


        String message;
        if (gameController.getGameState() == GameController.GameState.VITORIA) {
            gc.setFill(Color.web("#0CF25D"));
            message = "VITÓRIA!";
        } else {
            gc.setFill(Color.web("#F29F05"));
            message = "DERROTA!";
        }
        gc.fillText(message, gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 50);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        long seconds = gameUpdater.getSurvivalTimer() / 1000;
        DecimalFormat df = new DecimalFormat("#.##");

        gc.fillText("Tempo de Jogo: " + seconds + " segundos", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2);
        gc.fillText("Total de Inimigos: " + gameController.getTotalEnemies(), gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 + 30);
    }
}
