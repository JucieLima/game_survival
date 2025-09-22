package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.Bullet;
import com.survival.survivalgame.models.Direction;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;
import javafx.scene.canvas.Canvas;

import java.util.Iterator;
import java.util.List;

/**
 * Manages the main game logic, including updating game objects and checking for win/loss conditions.
 */
public class GameUpdater {

    private final GameController gameController;
    private final Player player;
    private final World world;
    private final InputHandler inputHandler;
    private final CollisionManager collisionManager;
    private final List<Bullet> bullets;
    private final Canvas gameCanvas;

    private long lastUpdateTime;
    private long survivalTimer = 0;
    // Win condition: survive for 60 seconds
    private static final long SURVIVE_TIME_SECONDS = 120;

    // Firing properties
    private final long fireRate = 150; // milliseconds between shots
    private long lastShotTime = 0;

    public GameUpdater(GameController gameController, Player player, World world, InputHandler inputHandler, CollisionManager collisionManager, List<Bullet> bullets, Canvas gameCanvas) {
        this.gameController = gameController;
        this.player = player;
        this.world = world;
        this.inputHandler = inputHandler;
        this.collisionManager = collisionManager;
        this.bullets = bullets;
        this.gameCanvas = gameCanvas;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * The main update loop.
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        survivalTimer += elapsedTime;

        // Player update
        player.update(inputHandler.getDirection(), world);

        // Enemy update
        world.getActiveAreas().forEach(area -> area.getEnemies().forEach(enemy -> enemy.update(player)));

        // Bullet update
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            // Remove bullets that go off-screen
            if (bullet.getX() < 0 || bullet.getX() > world.getGridWidth() * world.getAreaWidth() ||
                    bullet.getY() < 0 || bullet.getY() > world.getGridHeight() * world.getAreaHeight()) {
                bulletIterator.remove();
            }
        }

        // Firing a bullet on mouse click
        if (inputHandler.isFiring() && player.getAmmo() > 0 && currentTime - lastShotTime > fireRate) {
            // Get player's position on the canvas
            double playerCanvasX = gameCanvas.getWidth() / 2;
            double playerCanvasY = gameCanvas.getHeight() / 2;

            // Get mouse position relative to the world
            double mouseWorldX = player.getX() + (inputHandler.getMouseX() - playerCanvasX);
            double mouseWorldY = player.getY() + (inputHandler.getMouseY() - playerCanvasY);

            // Calculate direction vector
            double dx = mouseWorldX - player.getX();
            double dy = mouseWorldY - player.getY();
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                Direction shootDirection = new Direction(dx / length, dy / length);
                Bullet newBullet = new Bullet(player.getX(), player.getY(), shootDirection, 10, 10);
                bullets.add(newBullet);

                // Decrement player's ammo and update last shot time
                player.setAmmo(player.getAmmo() - 1);
                lastShotTime = currentTime;
            }
        }

        // Collision check
        collisionManager.checkCollisions();

        // Game state checks
        if (!player.isAlive() || survivalTimer >= SURVIVE_TIME_SECONDS * 1000) {
            gameController.endGame(GameController.GameState.DERROTA);
        }

        // Winning condition: defeat all enemies OR survive for the required time
        if (gameController.getTotalEnemies() <= 0) {
            gameController.endGame(GameController.GameState.VITORIA);
        }
    }

    public long getSurvivalTimer() {
        return survivalTimer;
    }
}
