package com.survival.survivalgame.models;

/**
 * Behavior where the enemy patrols between two points.
 */
public class PatrolBehavior extends Behavior {
    private final double speed;
    private final double point1X;
    private final double point1Y;
    private final double point2X;
    private final double point2Y;
    private boolean movingToPoint1 = true;

    public PatrolBehavior(double speed, double point1X, double point1Y, double point2X, double point2Y) {
        this.speed = speed;
        this.point1X = point1X;
        this.point1Y = point1Y;
        this.point2X = point2X;
        this.point2Y = point2Y;
    }

    @Override
    public void update(Enemy enemy, Player player) {
        double targetX, targetY;
        if (movingToPoint1) {
            targetX = point1X;
            targetY = point1Y;
        } else {
            targetX = point2X;
            targetY = point2Y;
        }

        double dx = targetX - enemy.getX();
        double dy = targetY - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // If the enemy is close to the target, switch to the other point
        if (distance < speed) {
            movingToPoint1 = !movingToPoint1;
        }

        // Move the enemy towards the target
        if (distance > 0) {
            double moveX = (dx / distance) * speed;
            double moveY = (dy / distance) * speed;
            enemy.setX(enemy.getX() + moveX);
            enemy.setY(enemy.getY() + moveY);
        }
    }
}
