package VISTAS;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Singleton that manages background music across all screens.
 * The music loops indefinitely and persists through scene changes
 * because the MediaPlayer instance lives here, not in any controller.
 */
public final class MusicManager {

    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private double currentVolume = 0.5;

    private MusicManager() {}

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    /**
     * Starts playing the background music in an infinite loop.
     * If music is already playing, this call is ignored.
     */
    public void play() {
        if (mediaPlayer == null) {
            try {
                var url = getClass().getResource("/Frost_Menu_Drift.mp3");
                if (url == null) {
                    System.out.println("⚠ Archivo de música no encontrado: /Frost_Menu_Drift.mp3");
                } else {
                    Media media = new Media(url.toExternalForm());
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    mediaPlayer.setVolume(0.5);
                    mediaPlayer.setOnError(() ->
                        System.out.println("⚠ Error reproduciendo la música: " + mediaPlayer.getError().getMessage())
                    );
                    mediaPlayer.play();
                }
            } catch (Exception e) {
                System.out.println("⚠ No se ha podido reproducir la música de fondo: " + e.getMessage());
            }
        }
    }

    /**
     * Stops the background music and releases the player.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    /**
     * Sets the volume (0.0 – 1.0).
     */
    public void setVolume(double volume) {
        currentVolume = Math.max(0.0, Math.min(1.0, volume));
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(currentVolume);
        }
    }

    /** Returns the current volume (0.0 – 1.0). */
    public double getVolume() {
        return currentVolume;
    }
}
