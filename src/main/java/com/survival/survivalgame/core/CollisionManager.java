package com.survival.survivalgame.core;

import com.survival.survivalgame.controllers.GameController;
import com.survival.survivalgame.models.AmmoBox;
import com.survival.survivalgame.models.Area;
import com.survival.survivalgame.models.Bullet;
import com.survival.survivalgame.models.Enemy;
import com.survival.survivalgame.models.FirstAidKit;
import com.survival.survivalgame.models.Item;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.World;

import java.util.Iterator;
import java.util.List;

/**
 * Manages all collision detection within the game, including player-enemy,
 * player-item, and bullet-enemy interactions.
 */
public class CollisionManager {
    private final Player player;
    private final World world;
    private final List<Bullet> bullets;
    private final GameController gameController;

    public CollisionManager(Player player, World world, List<Bullet> bullets, GameController gameController) {
        this.player = player;
        this.world = world;
        this.bullets = bullets;
        this.gameController = gameController;
    }

    /**
     * Checks for all types of collisions in the game.
     */
    public void checkCollisions() {
        checkPlayerEnemyCollisions();
        checkPlayerItemCollisions();
        checkBulletEnemyCollisions();
    }

    /**
     * Checks for collisions between the player and all active enemies.
     */
    private void checkPlayerEnemyCollisions() {
        for (Area area : world.getActiveAreas()) {
            // Use an iterator for safe removal during iteration
            Iterator<Enemy> enemyIterator = area.getEnemies().iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                double dx = player.getX() - enemy.getX();
                double dy = player.getY() - enemy.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Assuming a simple circular collision model
                if (distance < player.getRadius() + enemy.getRadius()) {
                    // Reduce player health based on enemy damage
                    player.takeDamage(enemy.getDamage());
                }
            }
        }
    }

    /**
     * Checks for collisions between the player and all active items.
     */
    private void checkPlayerItemCollisions() {
        for (Area area : world.getActiveAreas()) {
            Iterator<Item> itemIterator = area.getItems().iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                double dx = player.getX() - item.getX();
                double dy = player.getY() - item.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Assuming a simple circular collision model
                if (distance < player.getRadius() + 10) { // 10 is a placeholder radius for the item
                    item.applyEffect(player);
                    itemIterator.remove(); // Remove the item from the list
                }
            }
        }
    }

    /**
     * Checks for collisions between bullets and enemies.
     */
    private void checkBulletEnemyCollisions() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            boolean bulletHit = false;
            // Iterate over active areas to find enemies
            for (Area area : world.getActiveAreas()) {
                Iterator<Enemy> enemyIterator = area.getEnemies().iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    double dx = bullet.getX() - enemy.getX();
                    double dy = bullet.getY() - enemy.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < bullet.getRadius() + enemy.getRadius()) {
                        enemy.takeDamage(bullet.getDamage());
                        bulletHit = true;

                        if (enemy.getHealth() <= 0) {
                            enemyIterator.remove();
                            gameController.decrementTotalEnemies();
                        }
                        break; // Exit the inner loop once a bullet hits an enemy
                    }
                }
                if (bulletHit) {
                    bulletIterator.remove();
                    break; // Exit the outer loop to process the next bullet
                }
            }
        }
    }
}
