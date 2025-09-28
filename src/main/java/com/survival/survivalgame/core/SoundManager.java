package com.survival.survivalgame.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    private final MediaPlayer backgroundMusicPlayer;
    private final Media gunshotSound;
    private final List<MediaPlayer> gunshotPlayers;
    private final MediaPlayer footstepsPlayer;
    private final MediaPlayer victoryThemePlayer;
    private final MediaPlayer defeatThemePlayer;
    private final Media ammoPickupSound;
    private final Media healthPickupSound;
    private boolean isFootstepsPlaying;

    public SoundManager() {
        try {
            // Carregar arquivos de áudio existentes
            backgroundMusicPlayer = createMediaPlayer("/com/survival/survivalgame/sounds/background_music.mp3", true);
            gunshotSound = createMedia("/com/survival/survivalgame/sounds/gunshot.wav");
            footstepsPlayer = createMediaPlayer("/com/survival/survivalgame/sounds/footsteps.wav", true);

            // Novos sons para vitória e derrota
            victoryThemePlayer = createMediaPlayer("/com/survival/survivalgame/sounds/victory_theme.mp3", false); // Sem loop
            defeatThemePlayer = createMediaPlayer("/com/survival/survivalgame/sounds/defeat_theme.mp3", false); // Sem loop

            // Novos sons para coleta de itens
            ammoPickupSound = createMedia("/com/survival/survivalgame/sounds/ammo_pickup.wav");
            healthPickupSound = createMedia("/com/survival/survivalgame/sounds/health_pickup.wav");

            // Inicializar pool de MediaPlayer para tiros
            gunshotPlayers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                MediaPlayer player = new MediaPlayer(gunshotSound);
                player.setOnEndOfMedia(() -> player.stop());
                gunshotPlayers.add(player);
            }

            isFootstepsPlaying = false;
        } catch (Exception e) {
            System.err.println("Failed to initialize SoundManager: " + e.getMessage());
            throw new RuntimeException("SoundManager initialization failed", e);
        }
    }

    private Media createMedia(String path) {
        try {
            return new Media(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            System.err.println("Failed to load audio from path: " + path);
            throw new RuntimeException("Audio loading failed for: " + path, e);
        }
    }

    private MediaPlayer createMediaPlayer(String path, boolean loop) {
        Media media = createMedia(path);
        MediaPlayer player = new MediaPlayer(media);
        if (loop) {
            player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        return player;
    }

    public void playBackgroundMusic() {
        try {
            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to play background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        try {
            backgroundMusicPlayer.stop();
        } catch (Exception e) {
            System.err.println("Failed to stop background music: " + e.getMessage());
        }
    }

    public void playGunshot() {
        try {
            for (MediaPlayer player : gunshotPlayers) {
                if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                    player.stop();
                    player.play();
                    return;
                }
            }
            MediaPlayer newPlayer = new MediaPlayer(gunshotSound);
            newPlayer.setOnEndOfMedia(() -> newPlayer.stop());
            newPlayer.play();
            gunshotPlayers.add(newPlayer);
        } catch (Exception e) {
            System.err.println("Failed to play gunshot sound: " + e.getMessage());
        }
    }

    public void playFootsteps() {
        try {
            if (!isFootstepsPlaying) {
                footstepsPlayer.play();
                isFootstepsPlaying = true;
            }
        } catch (Exception e) {
            System.err.println("Failed to play footsteps sound: " + e.getMessage());
        }
    }

    public void stopFootsteps() {
        try {
            if (isFootstepsPlaying) {
                footstepsPlayer.stop();
                isFootstepsPlaying = false;
            }
        } catch (Exception e) {
            System.err.println("Failed to stop footsteps sound: " + e.getMessage());
        }
    }

    public void playVictoryTheme() {
        try {
            stopAll(); // Para todos os outros sons antes de tocar o tema de vitória
            victoryThemePlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to play victory theme: " + e.getMessage());
        }
    }

    public void playDefeatTheme() {
        try {
            stopAll(); // Para todos os outros sons antes de tocar o tema de derrota
            defeatThemePlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to play defeat theme: " + e.getMessage());
        }
    }

    public void playAmmoPickup() {
        try {
            MediaPlayer player = new MediaPlayer(ammoPickupSound);
            player.setOnEndOfMedia(() -> player.stop());
            player.play();
        } catch (Exception e) {
            System.err.println("Failed to play ammo pickup sound: " + e.getMessage());
        }
    }

    public void playHealthPickup() {
        try {
            MediaPlayer player = new MediaPlayer(healthPickupSound);
            player.setOnEndOfMedia(() -> player.stop());
            player.play();
        } catch (Exception e) {
            System.err.println("Failed to play health pickup sound: " + e.getMessage());
        }
    }

    public void stopAll() {
        try {
            backgroundMusicPlayer.stop();
            for (MediaPlayer player : gunshotPlayers) {
                player.stop();
            }
            footstepsPlayer.stop();
            victoryThemePlayer.stop();
            defeatThemePlayer.stop();
            isFootstepsPlaying = false;
        } catch (Exception e) {
            System.err.println("Failed to stop all sounds: " + e.getMessage());
        }
    }
}