package com.survival.survivalgame.core;

import com.survival.survivalgame.models.Direction;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Handles all keyboard and mouse input for the game.
 * It tracks which movement keys are currently pressed and provides a normalized
 * direction vector to the game logic, as well as handling mouse clicks for firing.
 */
public class InputHandler {

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

    public InputHandler() {
        this.currentState = PlayerState.IDLE;
        this.isMoving = false;
    }

    /**
     * Updates the player state based on movement and firing inputs.
     */
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

    /**
     * Sets up event handlers on the game scene to listen for key presses, releases, and mouse clicks.
     * @param scene The game scene to attach the handlers to.
     */
    public void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);
        scene.setOnMousePressed(this::handleMousePress);
        scene.setOnMouseReleased(this::handleMouseRelease);
        // Removido handleMouseMove, pois a direção do tiro agora vem de lastDirection
    }

    /**
     * Handles key press events, setting the corresponding movement flag to true.
     * @param event The key event.
     */
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
            default:
                break;
        }
    }

    /**
     * Handles key release events, setting the corresponding movement flag to false.
     * @param event The key event.
     */
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

    /**
     * Handles mouse press events, setting the firing flag to true.
     * @param event The mouse event.
     */
    private void handleMousePress(MouseEvent event) {
        this.isFiring = true;
        this.lastFiringTime = System.nanoTime();
    }

    /**
     * Handles mouse release events, setting the firing flag to false.
     * @param event The mouse event.
     */
    private void handleMouseRelease(MouseEvent event) {
        this.isFiring = false;
    }

    /**
     * Calculates and returns a normalized direction vector based on the currently pressed keys.
     * Updates isMoving based on whether any movement keys are pressed.
     * @return A Direction object representing the player's intended movement.
     */
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

        // Atualizar isMoving
        isMoving = (dx != 0 || dy != 0);

        // Normalize the vector to ensure consistent speed in all directions
        if (isMoving) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
        }

        return new Direction(dx, dy);
    }

    // Getters for firing state
    public boolean isFiring() {
        return isFiring;
    }
}