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

    /**
     * Sets up event handlers on the game scene to listen for key presses, releases, and mouse clicks.
     * @param scene The game scene to attach the handlers to.
     */
    public void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);
        scene.setOnMousePressed(this::handleMousePress);
        scene.setOnMouseReleased(this::handleMouseRelease);
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
     * Handles mouse press events, setting the firing flag to true and storing the mouse coordinates.
     * @param event The mouse event.
     */
    private void handleMousePress(MouseEvent event) {
        this.isFiring = true;
        this.mouseX = event.getX();
        this.mouseY = event.getY();
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

        // Normalize the vector to ensure consistent speed in all directions
        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
        }

        return new Direction(dx, dy);
    }

    // Getters for mouse state
    public boolean isFiring() {
        return isFiring;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }
}
