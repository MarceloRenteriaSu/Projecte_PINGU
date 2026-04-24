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
        if (mediaPlayer != null) return; // already playing

        try {
            String path = getClass().getResource("/extras/Frost-Menu-Drift.ogg").toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("No s'ha pogut reproduir la música de fons: " + e.getMessage());
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
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }
}
