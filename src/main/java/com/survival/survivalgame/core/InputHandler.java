package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.Direction;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class InputHandler {
    private final GameController gameController; // Adicionado para verificar o estado do jogo
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean isFiring;
    private double mouseX;
    private double mouseY;
    private PlayerState currentState;
    private boolean isMoving;
    private long lastFiringTime = 0;
    private static final long FIRING_DURATION = 200_000_000; // 200ms
    private Runnable startGameCallback;
    private Runnable restartGameCallback;

    public InputHandler(GameController gameController) {
        this.gameController = gameController;
        this.currentState = PlayerState.IDLE;
        this.isMoving = false;
    }

    public void updateState() {
        boolean isCurrentlyFiring = isFiring || (System.nanoTime() - lastFiringTime < FIRING_DURATION);
        if (isMoving && isCurrentlyFiring) {
            currentState = PlayerState.MOVING_SHOOTING;
        } else if (isCurrentlyFiring) {
            currentState = PlayerState.IDLE_SHOOTING;
        } else if (isMoving) {
            currentState = PlayerState.MOVING;
        } else {
            currentState = PlayerState.IDLE;
        }
    }

    public PlayerState getCurrentState() {
        return currentState;
    }

    public void setupInputHandlers(Scene scene, Runnable startGameCallback, Runnable restartGameCallback) {
        this.startGameCallback = startGameCallback;
        this.restartGameCallback = restartGameCallback;
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);
        scene.setOnMousePressed(this::handleMousePress);
        scene.setOnMouseReleased(this::handleMouseRelease);
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                upPressed = true;
                break;
            case S:
                downPressed = true;
                break;
            case A:
                leftPressed = true;
                break;
            case D:
                rightPressed = true;
                break;
            case SPACE:
                if (gameController.getGameState() == GameController.GameState.AGUARDANDO && startGameCallback != null) {
                    startGameCallback.run();
                } else if (gameController.getGameState() == GameController.GameState.DERROTA && restartGameCallback != null) {
                    restartGameCallback.run();
                }
                break;
            default:
                break;
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                upPressed = false;
                break;
            case S:
                downPressed = false;
                break;
            case A:
                leftPressed = false;
                break;
            case D:
                rightPressed = false;
                break;
            default:
                break;
        }
    }

    private void handleMousePress(MouseEvent event) {
        this.isFiring = true;
        this.lastFiringTime = System.nanoTime();
    }

    private void handleMouseRelease(MouseEvent event) {
        this.isFiring = false;
    }

    public Direction getDirection() {
        double dx = 0;
        double dy = 0;

        if (upPressed) {
            dy -= 1;
        }
        if (downPressed) {
            dy += 1;
        }
        if (leftPressed) {
            dx -= 1;
        }
        if (rightPressed) {
            dx += 1;
        }

        isMoving = (dx != 0 || dy != 0);

        if (isMoving) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
        }

        return new Direction(dx, dy);
    }

    public boolean isFiring() {
        return isFiring;
    }
}