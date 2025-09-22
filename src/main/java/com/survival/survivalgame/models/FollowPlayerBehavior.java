package com.survival.survivalgame.models;

/**
 * Behavior where the enemy follows the player.
 */
public class FollowPlayerBehavior extends Behavior {
    private final double speed;

    public FollowPlayerBehavior(double speed) {
        this.speed = speed;
    }

    @Override
    public void update(Enemy enemy, Player player) {
        double dx = player.getX() - enemy.getX();
        double dy = player.getY() - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            double moveX = (dx / distance) * speed;
            double moveY = (dy / distance) * speed;

            double newX = enemy.getX() + moveX;
            double newY = enemy.getY() + moveY;

            // Check if the new position is within the parent area's boundaries
            if (newX >= enemy.getParentArea().getGridX() * 900 && newX <= (enemy.getParentArea().getGridX() * 900 + 900) - 30 &&
                    newY >= enemy.getParentArea().getGridY() * 600 && newY <= (enemy.getParentArea().getGridY() * 600 + 600) - 30) {
                enemy.setX(newX);
                enemy.setY(newY);
            }
        }
    }
}
