package com.survival.survivalgame.models;

/**
 * Behavior that switches between patrolling and following the player based on proximity.
 */
public class SwitchingBehavior extends Behavior {
    private final Behavior patrolBehavior;
    private final Behavior followBehavior;
    private final double detectionRange;

    public SwitchingBehavior(Behavior patrolBehavior, Behavior followBehavior, double detectionRange) {
        this.patrolBehavior = patrolBehavior;
        this.followBehavior = followBehavior;
        this.detectionRange = detectionRange;
    }

    @Override
    public void update(Enemy enemy, Player player) {
        double dx = player.getX() - enemy.getX();
        double dy = player.getY() - enemy.getY();
        double distanceToPlayer = Math.sqrt(dx * dx + dy * dy);

        if (distanceToPlayer < detectionRange) {
            followBehavior.update(enemy, player);
        } else {
            patrolBehavior.update(enemy, player);
        }
    }
}
