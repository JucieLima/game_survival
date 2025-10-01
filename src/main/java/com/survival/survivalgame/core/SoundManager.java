package com.survival.survivalgame.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
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

    private static final double BACKGROUND_MUSIC_VOLUME = 0.2;
    private static final double FOOTSTEPS_VOLUME = 1.0;
    private static final double GUNSHOT_VOLUME = 0.7;
    private static final double THEME_VOLUME = 0.5;
    private static final double PICKUP_VOLUME = 0.6;

    public SoundManager() {
        Media tempMedia;
        MediaPlayer tempPlayer;

        gunshotPlayers = new ArrayList<>();
        isFootstepsPlaying = false;

        // Música de fundo
        tempPlayer = tryCreateMediaPlayer("/com/survival/survivalgame/sounds/background_music.mp3", true, BACKGROUND_MUSIC_VOLUME);
        backgroundMusicPlayer = tempPlayer;

        // Tiros
        tempMedia = tryCreateMedia("/com/survival/survivalgame/sounds/gunshot.wav");
        gunshotSound = tempMedia;
        if (gunshotSound != null) {
            for (int i = 0; i < 5; i++) {
                MediaPlayer player = new MediaPlayer(gunshotSound);
                player.setVolume(GUNSHOT_VOLUME);
                player.setOnEndOfMedia(player::stop);
                gunshotPlayers.add(player);
            }
        }

        // Passos
        tempPlayer = tryCreateMediaPlayer("/com/survival/survivalgame/sounds/footsteps.wav", true, FOOTSTEPS_VOLUME);
        footstepsPlayer = tempPlayer;

        // Temas vitória/derrota
        tempPlayer = tryCreateMediaPlayer("/com/survival/survivalgame/sounds/victory_theme.mp3", false, THEME_VOLUME);
        victoryThemePlayer = tempPlayer;

        tempPlayer = tryCreateMediaPlayer("/com/survival/survivalgame/sounds/defeat_theme.mp3", false, THEME_VOLUME);
        defeatThemePlayer = tempPlayer;

        // Sons de coleta
        ammoPickupSound = tryCreateMedia("/com/survival/survivalgame/sounds/ammo_pickup.wav");
        healthPickupSound = tryCreateMedia("/com/survival/survivalgame/sounds/health_pickup.wav");
    }

    // --- Métodos auxiliares ---
    private Media tryCreateMedia(String path) {
        try {
            // Tenta primeiro com getClass().getResource (caminho absoluto)
            URL resource = getClass().getResource(path);
            if (resource == null) {
                System.err.println("Recurso não encontrado com getClass().getResource: " + path);
                // Tenta com getClass().getClassLoader().getResource (caminho relativo)
                String cleanPath = path.startsWith("/") ? path.substring(1) : path;
                resource = getClass().getClassLoader().getResource(cleanPath);
                if (resource == null) {
                    System.err.println("Recurso não encontrado com getClass().getClassLoader().getResource: " + cleanPath);
                    System.err.println("ClassLoader: " + getClass().getClassLoader());
                    return null;
                }
            }
            System.out.println("Recurso encontrado: " + resource.toExternalForm());
            return new Media(resource.toExternalForm());
        } catch (Exception e) {
            System.err.println("Falha ao carregar áudio: " + path + " | " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private MediaPlayer tryCreateMediaPlayer(String path, boolean autoPlay, double volume) {
        Media media = tryCreateMedia(path);
        if (media == null) return null;
        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(autoPlay ? MediaPlayer.INDEFINITE : 1);
        player.setVolume(volume);
        return player;
    }

    // --- Métodos de reprodução ---
    public void playBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            stopAll();
            backgroundMusicPlayer.play();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) backgroundMusicPlayer.stop();
    }

    public void playGunshot() {
        if (gunshotPlayers.isEmpty()) return;
        for (MediaPlayer player : gunshotPlayers) {
            if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                player.stop();
                player.play();
                return;
            }
        }
        // Se todos ocupados, cria temporário
        MediaPlayer newPlayer = new MediaPlayer(gunshotSound);
        newPlayer.setVolume(GUNSHOT_VOLUME);
        newPlayer.setOnEndOfMedia(newPlayer::stop);
        newPlayer.play();
        gunshotPlayers.add(newPlayer);
    }

    public void playFootsteps() {
        if (footstepsPlayer != null && !isFootstepsPlaying) {
            footstepsPlayer.play();
            isFootstepsPlaying = true;
        }
    }

    public void stopFootsteps() {
        if (footstepsPlayer != null && isFootstepsPlaying) {
            footstepsPlayer.stop();
            isFootstepsPlaying = false;
        }
    }

    public void playVictoryTheme() {
        if (victoryThemePlayer != null) {
            stopAll();
            victoryThemePlayer.play();
        }
    }

    public void playDefeatTheme() {
        if (defeatThemePlayer != null) {
            stopAll();
            defeatThemePlayer.play();
        }
    }

    public void playAmmoPickup() {
        if (ammoPickupSound != null) {
            MediaPlayer player = new MediaPlayer(ammoPickupSound);
            player.setVolume(PICKUP_VOLUME);
            player.setOnEndOfMedia(player::stop);
            player.play();
        }
    }

    public void playHealthPickup() {
        if (healthPickupSound != null) {
            MediaPlayer player = new MediaPlayer(healthPickupSound);
            player.setVolume(PICKUP_VOLUME);
            player.setOnEndOfMedia(player::stop);
            player.play();
        }
    }

    public void stopAll() {
        if (backgroundMusicPlayer != null) backgroundMusicPlayer.stop();
        for (MediaPlayer player : gunshotPlayers) player.stop();
        if (footstepsPlayer != null) footstepsPlayer.stop();
        if (victoryThemePlayer != null) victoryThemePlayer.stop();
        if (defeatThemePlayer != null) defeatThemePlayer.stop();
        isFootstepsPlaying = false;
    }
}
