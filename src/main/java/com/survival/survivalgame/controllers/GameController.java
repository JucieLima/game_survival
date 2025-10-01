package com.survival.survivalgame.controllers;

import com.survival.survivalgame.core.*;
import com.survival.survivalgame.models.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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
    private int phaseLevel;
    private List<JSONObject> phaseConfigs;

    public enum GameState {
        AGUARDANDO, JOGANDO, VITORIA, DERROTA
    }

    private GameState gameState;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.gc = gameCanvas.getGraphicsContext2D();
        gameCanvas.widthProperty().bind(((StackPane) gameCanvas.getParent()).widthProperty());
        gameCanvas.heightProperty().bind(((StackPane) gameCanvas.getParent()).heightProperty());
        gameCanvas.setFocusTraversable(true);

        // Carregar configurações de fases
        loadPhaseConfigs();

        // Initialize game objects
        this.player = new Player(450, 300, 100, 50);
        this.world = new World(player);
        player.setWorld(world);
        this.bullets = new ArrayList<>();
        this.phaseLevel = 1;
        this.gameState = GameState.AGUARDANDO;
        this.totalEnemies = 0;

        // Initialize core game systems
        SoundManager soundManager = new SoundManager();
        this.inputHandler = new InputHandler(this);
        this.collisionManager = new CollisionManager(player, world, bullets, this, soundManager);
        this.gameUpdater = new GameUpdater(this, player, world, inputHandler, collisionManager, bullets, soundManager, this::nextPhase);
        this.gameRenderer = new GameRenderer(this, gameUpdater, gc, world, player, gameCanvas, bullets, inputHandler);

        // Set up input handler
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                inputHandler.setupInputHandlers(newScene, this::startGame, this::restartGame);
            }
        });

        // Iniciar o loop principal
        startGameLoop();
    }

    private void loadPhaseConfigs() {
        String path = "/com/survival/survivalgame/phases.json";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Recurso não encontrado: " + path);
                throw new RuntimeException("phases.json not found in classpath!");
            }
            System.out.println("Recurso encontrado: " + path);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            phaseConfigs = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                phaseConfigs.add(jsonArray.getJSONObject(i));
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Falha ao carregar phases.json: " + e.getMessage());
            e.printStackTrace();
            // Configuração padrão
            phaseConfigs = new ArrayList<>();
            JSONObject defaultPhase = new JSONObject()
                    .put("phaseLevel", 1)
                    .put("baseEnemies", 1)
                    .put("maxEnemies", 3)
                    .put("baseItems", 2)
                    .put("minItems", 1)
                    .put("enemyIncreaseFactor", 0.5)
                    .put("itemDecreaseFactor", 0.3)
                    .put("patrolSpeedBase", 1.0)
                    .put("followSpeedBase", 1.5)
                    .put("detectionRadiusBase", 200.0);
            phaseConfigs.add(defaultPhase);
        }
    }


    private void startGame() {
        if (gameState != GameState.AGUARDANDO) return;
        gameState = GameState.JOGANDO;
        gameUpdater.resetSurvivalTimer(); // Reiniciar o temporizador
        totalEnemies = spawnObjectsRandomly();
    }

    private void restartGame() {
        if (gameState != GameState.DERROTA) return;

        // Resetar para a primeira fase
        phaseLevel = 1;
        player.setX(450);
        player.setY(300);
        player.setHealth(100);
        player.setAmmo(50);
        totalEnemies = 0;
        bullets.clear();
        world.getActiveAreas().forEach(area -> {
            area.getEnemies().clear();
            area.getItems().clear();
        });
        gameUpdater.resetSurvivalTimer(); // Reiniciar o temporizador

        gameState = GameState.AGUARDANDO;
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
        totalEnemies = 0;
        double areaWidth = world.getAreaWidth();
        double areaHeight = world.getAreaHeight();

        // Obter configuração da fase atual
        JSONObject phaseConfig = phaseConfigs.get(Math.min(phaseLevel - 1, phaseConfigs.size() - 1));
        int baseEnemies = phaseConfig.getInt("baseEnemies");
        int maxEnemies = phaseConfig.getInt("maxEnemies");
        int baseItems = phaseConfig.getInt("baseItems");
        int minItems = phaseConfig.getInt("minItems");
        double patrolSpeedBase = phaseConfig.getDouble("patrolSpeedBase");
        double followSpeedBase = phaseConfig.getDouble("followSpeedBase");
        double detectionRadiusBase = phaseConfig.getDouble("detectionRadiusBase");

        for (int i = 0; i < world.getGridWidth(); i++) {
            for (int j = 0; j < world.getGridHeight(); j++) {
                Area currentArea = world.getAreaAt(i * areaWidth, j * areaHeight);

                // Spawn enemies
                int numEnemies = random.nextInt(maxEnemies - baseEnemies + 1) + baseEnemies;
                totalEnemies += numEnemies;
                for (int k = 0; k < numEnemies; k++) {
                    double enemyX = i * areaWidth + random.nextInt((int) areaWidth);
                    double enemyY = j * areaHeight + random.nextInt((int) areaHeight);

                    double patrolX1 = i * areaWidth + random.nextInt((int) areaWidth);
                    double patrolY1 = j * areaHeight + random.nextInt((int) areaHeight);
                    double patrolX2 = i * areaWidth + random.nextInt((int) areaWidth);
                    double patrolY2 = j * areaHeight + random.nextInt((int) areaHeight);

                    double patrolSpeed = patrolSpeedBase + random.nextDouble();
                    double followSpeed = followSpeedBase + random.nextDouble();
                    double detectionRadius = detectionRadiusBase + random.nextDouble() * 100;

                    Behavior patrolBehavior = new PatrolBehavior(patrolSpeed, patrolX1, patrolY1, patrolX2, patrolY2);
                    Behavior followBehavior = new FollowPlayerBehavior(followSpeed);
                    Behavior switchingBehavior = new SwitchingBehavior(patrolBehavior, followBehavior, detectionRadius);

                    currentArea.addEnemy(new Enemy(enemyX, enemyY, 50, switchingBehavior, currentArea));
                }

                // Spawn items
                int numItems = random.nextInt(baseItems - minItems + 1) + minItems;
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
        return totalEnemies;
    }

    private void nextPhase() {
        phaseLevel++;
        if (phaseLevel > phaseConfigs.size()) {
            gameState = GameState.VITORIA;
            return;
        }

        // Resetar estado do jogo
        player.setX(450);
        player.setY(300);
        player.setHealth(100);
        player.setAmmo(50);
        totalEnemies = 0;
        bullets.clear();
        world.getActiveAreas().forEach(area -> {
            area.getEnemies().clear();
            area.getItems().clear();
        });
        gameUpdater.resetSurvivalTimer(); // Reiniciar o temporizador

        gameState = GameState.AGUARDANDO;
    }

    public void endGame(GameState finalState) {
        this.gameState = finalState;
        System.out.println("Fim de Jogo! Estado: " + finalState.name());
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getTotalEnemies() {
        return totalEnemies;
    }

    public void decrementTotalEnemies() {
        if (totalEnemies > 0) {
            totalEnemies--;
            System.out.println("Inimigo eliminado! Total restante: " + totalEnemies);
            if (totalEnemies <= 0 && gameState == GameState.JOGANDO) {
                nextPhase();
            }
        }
    }

    public int getPhaseLevel() {
        return phaseLevel;
    }
}