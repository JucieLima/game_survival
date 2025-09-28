package com.survival.survivalgame.core;

import com.survival.survivalgame.models.Area;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class WorldRenderer {
    private final GraphicsContext gc;
    private final World world;

    public WorldRenderer(GraphicsContext gc, World world) {
        this.gc = gc;
        this.world = world;
    }

    public void render(Player player) {
        List<Area> activeAreas = world.getActiveAreas();
        Area currentArea = world.getAreaAt(player.getX(), player.getY());

        for (Area area : world.getAllAreas()) {
            gc.setStroke(Color.web("#444444"));
            gc.setLineWidth(2);

            if (area == currentArea || activeAreas.contains(area)) {
                gc.setFill(Color.web("#bdc8c0"));
            } else {
                gc.setFill(Color.web("#333333"));
            }

            gc.fillRect(area.getGridX() * world.getAreaWidth(), area.getGridY() * world.getAreaHeight(), world.getAreaWidth(), world.getAreaHeight());
            gc.strokeRect(area.getGridX() * world.getAreaWidth(), area.getGridY() * world.getAreaHeight(), world.getAreaWidth(), world.getAreaHeight());
        }
    }
}