package com.survival.survivalgame.controllers;

import com.survival.survivalgame.core.*;
import com.survival.survivalgame.models.AmmoBox;
import com.survival.survivalgame.models.Area;
import com.survival.survivalgame.models.Enemy;
import com.survival.survivalgame.models.FirstAidKit;
import com.survival.survivalgame.models.FollowPlayerBehavior;
import com.survival.survivalgame.models.PatrolBehavior;
import com.survival.survivalgame.models.Player;
import com.survival.survivalgame.models.Behavior;
import com.survival.survivalgame.models.SwitchingBehavior;
import com.survival.survivalgame.models.World;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Random;
import com.survival.survivalgame.models.Bullet;

public class GameController implements Initializable {

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private Player player;
    private World world;
    private InputHandler inputHandler;
    private GameUpdater gameUpdater;
    private GameRenderer gameRenderer;
    private CollisionManager collisionManager;
    private List<Bullet> bullets;
    private final Random random = new Random();
    private int totalEnemies;

    // Game state management
    public enum GameState {
        JOGANDO, VITORIA, DERROTA
    }

    private GameState gameState;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.gc = gameCanvas.getGraphicsContext2D();
        gameCanvas.widthProperty().bind(((StackPane) gameCanvas.getParent()).widthProperty());
        gameCanvas.heightProperty().bind(((StackPane) gameCanvas.getParent()).heightProperty());
        gameCanvas.setFocusTraversable(true);

        // Initialize game objects
        this.player = new Player(450, 300, 100, 50);
        this.world = new World(player);
        player.setWorld(world);
        this.bullets = new ArrayList<>();

        // Spawns enemies and items randomly
        this.totalEnemies = spawnObjectsRandomly();

        // Initialize core game systems
        SoundManager soundManager  = new SoundManager();
        this.gameState = GameState.JOGANDO;
        this.inputHandler = new InputHandler();
        this.collisionManager = new CollisionManager(player, world, bullets, this, soundManager);
        this.gameUpdater = new GameUpdater(this, player, world, inputHandler, collisionManager, bullets, soundManager);
        this.gameRenderer = new GameRenderer(this, gameUpdater, gc, world, player, gameCanvas, bullets, inputHandler);

        // Set up input handler after the canvas has been added to a Scene
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                inputHandler.setupInputHandlers(newScene);
            }
        });

        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameState == GameState.JOGANDO) {
                    gameUpdater.update();
                }
                gameRenderer.render();
            }
        };
        gameLoop.start();
    }

    private int spawnObjectsRandomly() {
        double areaWidth = world.getAreaWidth();
        double areaHeight = world.getAreaHeight();
        int enemyCount = 0;

        for (int i = 0; i < world.getGridWidth(); i++) {
            for (int j = 0; j < world.getGridHeight(); j++) {
                Area currentArea = world.getAreaAt(i * areaWidth, j * areaHeight);

                // Spawn enemies
                int numEnemies = random.nextInt(3) + 1; // 1 to 3 enemies per area
                enemyCount += numEnemies;
                for (int k = 0; k < numEnemies; k++) {
                    double enemyX = i * areaWidth + random.nextInt((int) areaWidth);
                    double enemyY = j * areaHeight + random.nextInt((int) areaHeight);

                    double patrolX1 = i * areaWidth + random.nextInt((int) areaWidth);
                    double patrolY1 = j * areaHeight + random.nextInt((int) areaHeight);
                    double patrolX2 = i * areaWidth + random.nextInt((int) areaWidth);
                    double patrolY2 = j * areaHeight + random.nextInt((int) areaHeight);

                    double patrolSpeed = 1.0 + random.nextDouble(); // 1.0 to 2.0
                    double followSpeed = 1.5 + random.nextDouble(); // 1.5 to 2.5
                    double detectionRadius = 200 + random.nextDouble() * 100; // 200 to 300

                    Behavior patrolBehavior = new PatrolBehavior(patrolSpeed, patrolX1, patrolY1, patrolX2, patrolY2);
                    Behavior followBehavior = new FollowPlayerBehavior(followSpeed);
                    Behavior switchingBehavior = new SwitchingBehavior(patrolBehavior, followBehavior, detectionRadius);

                    currentArea.addEnemy(new Enemy(enemyX, enemyY, 50, switchingBehavior, currentArea));
                }

                // Spawn items
                int numItems = random.nextInt(2) + 1; // 1 to 2 items per area
                for (int k = 0; k < numItems; k++) {
                    double itemX = i * areaWidth + random.nextInt((int) areaWidth);
                    double itemY = j * areaHeight + random.nextInt((int) areaHeight);

                    if (random.nextBoolean()) {
                        currentArea.addItem(new FirstAidKit(itemX, itemY, 20));
                    } else {
                        currentArea.addItem(new AmmoBox(itemX, itemY, 30));
                    }
                }
            }
        }
        return enemyCount;
    }

    /**
     * Changes the game state and handles end-of-game logic.
     * @param finalState The final state of the game (VITORIA or DERROTA).
     */
    public void endGame(GameState finalState) {
        this.gameState = finalState;
        System.out.println("Fim de Jogo! Estado: " + finalState.name());
        // No momento, apenas paramos de atualizar a lógica, mas a renderização continua para mostrar a tela final.
    }

    /**
     * Gets the current game state.
     * @return The current GameState.
     */
    public GameState getGameState() {
        return gameState;
    }

    public int getTotalEnemies() {
        return totalEnemies;
    }

    public void decrementTotalEnemies() {
        totalEnemies--;
    }
}
