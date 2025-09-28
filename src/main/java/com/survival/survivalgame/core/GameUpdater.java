package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.Bullet;
import com.survival.survivalgame.models.Direction;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;
import javafx.scene.canvas.Canvas;

import java.util.Iterator;
import java.util.List;

public class GameUpdater {
    private final GameController gameController;
    private final Player player;
    private final World world;
    private final InputHandler inputHandler;
    private final CollisionManager collisionManager;
    private final List<Bullet> bullets;
    private final SoundManager soundManager;

    private long lastUpdateTime;
    private long survivalTimer = 0;
    private static final long SURVIVE_TIME_SECONDS = 120;

    private final long fireRate = 150;
    private long lastShotTime = 0;
    private Direction lastDirection = new Direction(0, -1);

    public GameUpdater(GameController gameController, Player player, World world, InputHandler inputHandler,
                       CollisionManager collisionManager, List<Bullet> bullets, SoundManager soundManager) {
        this.gameController = gameController;
        this.player = player;
        this.world = world;
        this.inputHandler = inputHandler;
        this.collisionManager = collisionManager;
        this.bullets = bullets;
        this.soundManager = soundManager;
        this.lastUpdateTime = System.currentTimeMillis();

        // Iniciar música de fundo
        soundManager.playBackgroundMusic();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        survivalTimer += elapsedTime;

        // Atualizar o estado do jogador
        inputHandler.updateState();

        // Controlar som de passos com base no estado
        PlayerState state = inputHandler.getCurrentState();
        if (state == PlayerState.MOVING || state == PlayerState.MOVING_SHOOTING) {
            soundManager.playFootsteps();
        } else {
            soundManager.stopFootsteps();
        }

        // Player update with debug output
        Direction direction = inputHandler.getDirection();
        player.update(direction, world);
        System.out.println("Player Position: (" + player.getX() + ", " + player.getY() + ")");

        // Update last direction if moving
        if (direction != null && (direction.getDx() != 0 || direction.getDy() != 0)) {
            lastDirection = direction;
        }

        // Enemy update
        world.getActiveAreas().forEach(area -> area.getEnemies().forEach(enemy -> enemy.update(player)));

        // Bullet update
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.getX() < 0 || bullet.getX() > world.getGridWidth() * world.getAreaWidth() ||
                    bullet.getY() < 0 || bullet.getY() > world.getGridHeight() * world.getAreaHeight()) {
                bulletIterator.remove();
            }
        }

        // Firing a bullet in the direction the player is moving or last moved
        if (inputHandler.isFiring() && player.getAmmo() > 0 && currentTime - lastShotTime > fireRate) {
            Bullet newBullet = new Bullet(player.getX(), player.getY(), lastDirection, 10, 10);
            bullets.add(newBullet);
            player.setAmmo(player.getAmmo() - 1);
            lastShotTime = currentTime;
            soundManager.playGunshot(); // Tocar som de tiro
        }

        // Collision check
        collisionManager.checkCollisions();

        // Game state checks
        if (!player.isAlive() || survivalTimer >= SURVIVE_TIME_SECONDS * 1000) {
            soundManager.playDefeatTheme(); // Tocar tema de derrota
            gameController.endGame(GameController.GameState.DERROTA);
        }

        if (gameController.getTotalEnemies() <= 0) {
            soundManager.playVictoryTheme(); // Tocar tema de vitória
            gameController.endGame(GameController.GameState.VITORIA);
        }
    }

    public long getSurvivalTimer() {
        return survivalTimer;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }
}