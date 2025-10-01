package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.Bullet;
import com.survival.survivalgame.models.Direction;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;
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
    private final Runnable nextPhaseCallback;

    private long lastUpdateTime;
    private long survivalTimer;
    private static final long SURVIVE_TIME_SECONDS = 120;

    private long lastShotTime = 0;
    private Direction lastDirection = new Direction(0, -1);

    public GameUpdater(GameController gameController, Player player, World world, InputHandler inputHandler,
                       CollisionManager collisionManager, List<Bullet> bullets, SoundManager soundManager,
                       Runnable nextPhaseCallback) {
        this.gameController = gameController;
        this.player = player;
        this.world = world;
        this.inputHandler = inputHandler;
        this.collisionManager = collisionManager;
        this.bullets = bullets;
        this.soundManager = soundManager;
        this.nextPhaseCallback = nextPhaseCallback;
        this.survivalTimer = 0;
        this.lastUpdateTime = System.currentTimeMillis();

        soundManager.playBackgroundMusic();
    }

    public void update() {
        if (gameController.getGameState() != GameController.GameState.JOGANDO) return;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;
        if (elapsedTime >= 0) {
            survivalTimer += elapsedTime;
        }
        lastUpdateTime = currentTime;

        inputHandler.updateState();
        PlayerState state = inputHandler.getCurrentState();
        if (state == PlayerState.MOVING || state == PlayerState.MOVING_SHOOTING) {
            soundManager.playFootsteps();
        } else {
            soundManager.stopFootsteps();
        }

        Direction direction = inputHandler.getDirection();
        player.update(direction, world);

        if (direction.getDx() != 0 || direction.getDy() != 0) {
            lastDirection = direction;
        }

        world.getActiveAreas().forEach(area -> area.getEnemies().forEach(enemy -> enemy.update(player)));

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.getX() < 0 || bullet.getX() > world.getGridWidth() * world.getAreaWidth() ||
                    bullet.getY() < 0 || bullet.getY() > world.getGridHeight() * world.getAreaHeight()) {
                bulletIterator.remove();
            }
        }

        long fireRate = 150;
        if (inputHandler.isFiring() && player.getAmmo() > 0 && currentTime - lastShotTime > fireRate) {
            Bullet newBullet = new Bullet(player.getX(), player.getY(), lastDirection, 10, 10);
            bullets.add(newBullet);
            player.setAmmo(player.getAmmo() - 1);
            lastShotTime = currentTime;
            soundManager.playGunshot();
        }

        collisionManager.checkCollisions();

        if (!player.isAlive() || survivalTimer >= SURVIVE_TIME_SECONDS * 1000) {
            soundManager.playDefeatTheme();
            gameController.endGame(GameController.GameState.DERROTA);
        } else if (gameController.getTotalEnemies() <= 0) {
            soundManager.playVictoryTheme();
            nextPhaseCallback.run();
        }
    }

    public long getSurvivalTimer() {
        return Math.max(0, survivalTimer);
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void resetSurvivalTimer() {
        survivalTimer = 0;
        lastUpdateTime = System.currentTimeMillis();
    }
}