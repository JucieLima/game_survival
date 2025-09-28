package com.survival.survivalgame.core;

import com.survival.survivalgame.models.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HUDRenderer {
    private final GraphicsContext gc;
    private final Player player;
    private final GameUpdater gameUpdater;
    private final Canvas gameCanvas;

    public HUDRenderer(GraphicsContext gc, Player player, GameUpdater gameUpdater, Canvas gameCanvas) {
        this.gc = gc;
        this.player = player;
        this.gameUpdater = gameUpdater;
        this.gameCanvas = gameCanvas;
    }

    public void render() {
        gc.setFill(Color.DARKRED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        gc.setFill(Color.web("#F29F05"));
        gc.fillText("Saúde: " + (int) player.getHealth(), 10, 30);
        gc.fillText("Munição: " + (int) player.getAmmo(), 10, 60);
        gc.setFill(Color.RED);
        gc.fillRect(120, 10, 100, 20);
        gc.setFill(Color.GREEN);
        gc.fillRect(120, 10, player.getHealth(), 20);

        long seconds = gameUpdater.getSurvivalTimer() / 1000;
        gc.setFill(Color.web("#F29F05"));
        gc.fillText("Tempo: " + seconds, gameCanvas.getWidth() - 120, 30);
    }
}