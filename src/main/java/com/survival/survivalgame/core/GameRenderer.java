package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.Bullet;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Coordinates the rendering operations, delegating to specialized renderers.
 */
public class GameRenderer {
    private final GameController gameController;
    private final GameUpdater gameUpdater;
    private final GraphicsContext gc;
    private final World world;
    private final Player player;
    private final Canvas gameCanvas;
    private final List<Bullet> bullets;
    private final InputHandler inputHandler;

    private final EntityRenderer entityRenderer;
    private final WorldRenderer worldRenderer;
    private final HUDRenderer hudRenderer;
    private final MinimapRenderer minimapRenderer;

    public GameRenderer(GameController gameController, GameUpdater gameUpdater, GraphicsContext gc, World world, Player player, Canvas gameCanvas, List<Bullet> bullets, InputHandler inputHandler) {
        this.gameController = gameController;
        this.gameUpdater = gameUpdater;
        this.gc = gc;
        this.world = world;
        this.player = player;
        this.gameCanvas = gameCanvas;
        this.bullets = bullets;
        this.inputHandler = inputHandler;

        this.entityRenderer = new EntityRenderer(gc, inputHandler, gameUpdater);
        this.worldRenderer = new WorldRenderer(gc, world);
        this.hudRenderer = new HUDRenderer(gc, player, gameUpdater, gameCanvas);
        this.minimapRenderer = new MinimapRenderer(gc, world, gameCanvas);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
            }
        }.start();
    }

    public void render() {
        gc.setFill(Color.web("#333333"));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.save();
        double cameraX = player.getX() - gameCanvas.getWidth() / 2;
        double cameraY = player.getY() - gameCanvas.getHeight() / 2;
        gc.translate(-cameraX, -cameraY);

        worldRenderer.render(player);
        entityRenderer.renderPlayer(player, inputHandler);
        entityRenderer.renderEnemies(world.getActiveAreas());
        entityRenderer.renderBullets(bullets);
        entityRenderer.renderItems(world.getActiveAreas());

        gc.restore();

        hudRenderer.render();
        minimapRenderer.render(player);

        if (gameController.getGameState() != GameController.GameState.JOGANDO) {
            drawEndGameScreen();
        }
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