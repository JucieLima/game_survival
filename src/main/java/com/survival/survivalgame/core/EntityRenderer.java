package com.survival.survivalgame.core;

import com.survival.survivalgame.models.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class EntityRenderer {
    private final GraphicsContext gc;
    private final GameUpdater gameUpdater;
    private final Image playerSpriteSheet;
    private final Image enemySpriteSheet;
    private final Image bulletImage;
    private final Image firstAidImage;
    private final Image ammoImage;

    private static final double FRAME_WIDTH = 216; // 1728 / 8 quadros
    private static final double FRAME_HEIGHT = 240; // Altura da spritesheet
    private static final double PLAYER_DISPLAY_WIDTH = 40; // Tamanho de exibição na tela
    private static final double PLAYER_DISPLAY_HEIGHT = 40; // Tamanho de exibição na tela
    private static final double ENEMY_FRAME_WIDTH = 80;
    private static final double ENEMY_FRAME_HEIGHT = 80;
    private static final double ENEMY_DISPLAY_SIZE = 40;
    private static final double BULLET_SIZE = 16;
    private static final double ITEM_SIZE = 20;

    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION = 200_000_000;
    private double lastAngle = 0.0;

    public EntityRenderer(GraphicsContext gc, InputHandler inputHandler, GameUpdater gameUpdater) {
        this.gc = gc;
        this.gameUpdater = gameUpdater;
        this.playerSpriteSheet = loadImage("/com/survival/survivalgame/images/player_spritesheet.png");
        this.enemySpriteSheet = loadImage("/com/survival/survivalgame/images/enemy_spritesheet.png");
        this.bulletImage = loadImage("/com/survival/survivalgame/images/bullet.png");
        this.firstAidImage = loadImage("/com/survival/survivalgame/images/firstaid.png");
        this.ammoImage = loadImage("/com/survival/survivalgame/images/ammo.png");

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                PlayerState state = inputHandler.getCurrentState();
                if ((state == PlayerState.MOVING || state == PlayerState.MOVING_SHOOTING) &&
                        now - lastFrameTime >= FRAME_DURATION) {
                    currentFrame = (currentFrame + 1) % 4; // 4 quadros para MOVING e MOVING_SHOOTING
                    lastFrameTime = now;
                } else if (state == PlayerState.IDLE || state == PlayerState.IDLE_SHOOTING) {
                    currentFrame = 0; // Reset para frame estático
                }
            }
        }.start();
    }

    private Image loadImage(String path) {
        var resource = getClass().getResourceAsStream(path);
        if (resource == null) {
            throw new RuntimeException("Image not found: " + path);
        }
        return new Image(resource);
    }

    public void renderPlayer(Player player, InputHandler inputHandler) {
        int frameIndex;
        PlayerState state = inputHandler.getCurrentState();

        // Mapear estados para frames da spritesheet
        frameIndex = switch (state) {
            case MOVING ->
                // Quadros 1-4 para MOVING
                    switch (currentFrame) {
                        case 1 -> 2;
                        case 2 -> 3;
                        case 3 -> 2; // Repete o quadro 2 para suavidade
                        default -> 1;
                    };
            case MOVING_SHOOTING ->
                // Quadros 6-7 para MOVING_SHOOTING
                    switch (currentFrame) {
                        case 1, 3 -> 7;
                        case 2 -> 6; // Repete para manter animação curta
                        default -> 6;
                    };
            case IDLE_SHOOTING -> 5; // Quadro 5 para IDLE_SHOOTING (estático)
            default -> 0; // Quadro 0 para IDLE (estático)
        };

        // Calcular rotação baseada na última direção (alinhada com o tiro)
        double angle = lastAngle;
        Direction direction = gameUpdater.getLastDirection();
        if (direction != null && (direction.getDx() != 0 || direction.getDy() != 0)) {
            double dx = direction.getDx();
            double dy = direction.getDy();
            angle = Math.toDegrees(Math.atan2(dy, dx));
            angle = (angle + 270) % 360; // Ajustar para alinhar com o sprite
            lastAngle = angle;
        }

        gc.save();
        double halfSize = PLAYER_DISPLAY_WIDTH / 2;
        gc.translate(player.getX(), player.getY());
        gc.rotate(angle);
        gc.translate(-player.getX(), -player.getY());

        gc.drawImage(
                playerSpriteSheet,
                frameIndex * FRAME_WIDTH, 0,
                FRAME_WIDTH, FRAME_HEIGHT,
                player.getX() - halfSize, player.getY() - halfSize,
                PLAYER_DISPLAY_WIDTH, PLAYER_DISPLAY_HEIGHT
        );

        gc.restore();
    }

    public void renderEnemies(List<Area> activeAreas) {
        int frameIndex = switch (currentFrame) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            default -> 0;
        };

        for (Area area : activeAreas) {
            for (Enemy enemy : area.getEnemies()) {
                double halfSize = ENEMY_DISPLAY_SIZE / 2;
                gc.drawImage(
                        enemySpriteSheet,
                        frameIndex * ENEMY_FRAME_WIDTH, 0,
                        ENEMY_FRAME_WIDTH, ENEMY_FRAME_HEIGHT,
                        enemy.getX() - halfSize, enemy.getY() - halfSize,
                        ENEMY_DISPLAY_SIZE, ENEMY_DISPLAY_SIZE
                );
            }
        }
    }

    public void renderBullets(List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            double halfSize = BULLET_SIZE / 2;
            gc.drawImage(bulletImage, bullet.getX() - halfSize, bullet.getY() - halfSize, BULLET_SIZE, BULLET_SIZE);
        }
    }

    public void renderItems(List<Area> activeAreas) {
        for (Area area : activeAreas) {
            for (Item item : area.getItems()) {
                double halfSize = ITEM_SIZE / 2;
                if (item instanceof FirstAidKit) {
                    gc.drawImage(firstAidImage, item.getX() - halfSize, item.getY() - halfSize, ITEM_SIZE, ITEM_SIZE);
                } else if (item instanceof AmmoBox) {
                    gc.drawImage(ammoImage, item.getX() - halfSize, item.getY() - halfSize, ITEM_SIZE, ITEM_SIZE);
                }
            }
        }
    }
}