package com.survival.survivalgame;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class ResourceTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Listar todos os recursos no classpath
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("com/survival/survivalgame");
            while (resources.hasMoreElements()) {
                System.out.println("Recurso encontrado no classpath: " + resources.nextElement());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar recursos: " + e.getMessage());
            e.printStackTrace();
        }

        // Testar phases.json
        String jsonPath = "/com/survival/survivalgame/phases.json";
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Recurso não encontrado: " + jsonPath);
            } else {
                System.out.println("Recurso encontrado: " + jsonPath);
                System.out.println(new String(is.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar phases.json: " + e.getMessage());
            e.printStackTrace();
        }

        // Testar todos os arquivos de áudio
        String[] audioFiles = {
                "/com/survival/survivalgame/sounds/background_music.mp3",
                "/com/survival/survivalgame/sounds/gunshot.wav",
                "/com/survival/survivalgame/sounds/footsteps.wav",
                "/com/survival/survivalgame/sounds/victory_theme.mp3",
                "/com/survival/survivalgame/sounds/defeat_theme.mp3",
                "/com/survival/survivalgame/sounds/ammo_pickup.wav",
                "/com/survival/survivalgame/sounds/health_pickup.wav"
        };

        for (String audioPath : audioFiles) {
            try {
                URL resource = getClass().getResource(audioPath);
                if (resource == null) {
                    System.err.println("Recurso não encontrado com getClass().getResource: " + audioPath);
                    String cleanPath = audioPath.startsWith("/") ? audioPath.substring(1) : audioPath;
                    resource = getClass().getClassLoader().getResource(cleanPath);
                    if (resource == null) {
                        System.err.println("Recurso não encontrado com getClass().getClassLoader().getResource: " + cleanPath);
                        continue;
                    }
                }
                System.out.println("Recurso encontrado: " + resource.toExternalForm());
                Media media = new Media(resource.toExternalForm());
                MediaPlayer player = new MediaPlayer(media);
                player.setOnError(() -> System.err.println("Erro no MediaPlayer para " + audioPath + ": " + player.getError().getMessage()));
                player.play();
                Thread.sleep(1000);
                player.stop();
            } catch (Exception e) {
                System.err.println("Erro ao carregar áudio: " + audioPath + " | " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Fechar a aplicação após os testes
        javafx.application.Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}