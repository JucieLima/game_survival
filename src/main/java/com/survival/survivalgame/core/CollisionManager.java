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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollisionManager {
    private final Player player;
    private final World world;
    private final List<Bullet> bullets;
    private final GameController gameController;
    private final SoundManager soundManager;

    public CollisionManager(Player player, World world, List<Bullet> bullets, GameController gameController, SoundManager soundManager) {
        this.player = player;
        this.world = world;
        this.bullets = bullets;
        this.gameController = gameController;
        this.soundManager = soundManager;
    }

    public void checkCollisions() {
        checkPlayerEnemyCollisions();
        checkPlayerItemCollisions();
        checkBulletEnemyCollisions();
    }

    private void checkPlayerEnemyCollisions() {
        for (Area area : world.getActiveAreas()) {
            Iterator<Enemy> enemyIterator = area.getEnemies().iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                double dx = player.getX() - enemy.getX();
                double dy = player.getY() - enemy.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < player.getRadius() + enemy.getRadius()) {
                    player.takeDamage(enemy.getDamage());
                }
            }
        }
    }

    private void checkPlayerItemCollisions() {
        for (Area area : world.getActiveAreas()) {
            Iterator<Item> itemIterator = area.getItems().iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                double dx = player.getX() - item.getX();
                double dy = player.getY() - item.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < player.getRadius() + 10) {
                    item.applyEffect(player);
                    if (item instanceof FirstAidKit) {
                        soundManager.playHealthPickup();
                    } else if (item instanceof AmmoBox) {
                        soundManager.playAmmoPickup();
                    }
                    itemIterator.remove();
                }
            }
        }
    }

    private void checkBulletEnemyCollisions() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            boolean bulletHit = false;

            for (Area area : world.getActiveAreas()) {
                Iterator<Enemy> enemyIterator = area.getEnemies().iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    double dx = bullet.getX() - enemy.getX();
                    double dy = bullet.getY() - enemy.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < bullet.getRadius() + enemy.getRadius()) {
                        enemy.takeDamage(bullet.getDamage());
                        bulletsToRemove.add(bullet);
                        if (enemy.getHealth() <= 0) {
                            enemiesToRemove.add(enemy);
                        }
                        bulletHit = true;
                        break;
                    }
                }
                if (bulletHit) {
                    break;
                }
            }
        }

        // Remover balas e inimigos após a iteração
        bullets.removeAll(bulletsToRemove);
        for (Area area : world.getActiveAreas()) {
            area.getEnemies().removeAll(enemiesToRemove);
        }

        // Decrementar totalEnemies após todas as remoções
        for (Enemy enemy : enemiesToRemove) {
            gameController.decrementTotalEnemies();
        }
    }
}