package VISTAS;

import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 * Loads the custom arrow cursor from cursor.png (left half of the sprite sheet)
 * and provides helpers to apply it to any Scene or Node.
 * The image is loaded once and cached for the lifetime of the application.
 */
public class CursorManager {

    private static ImageCursor cursor = null;
    private static boolean loaded     = false;

    private CursorManager() {}

    private static ImageCursor get() {
        if (!loaded) {
            loaded = true;
            try {
                Image full = new Image(CursorManager.class.getResourceAsStream("cursor.png"));
                int fw = (int) full.getWidth();
                int fh = (int) full.getHeight();
                int hw = fw / 2;
                PixelReader pr = full.getPixelReader();
                WritableImage arrowImg = new WritableImage(pr, 0, 0, hw, fh);
                // Hotspot at the arrow tip (upper-left area of the sprite)
                cursor = new ImageCursor(arrowImg, hw * 0.20, fh * 0.10);
            } catch (Exception e) {
                System.out.println("CursorManager: could not load cursor.png — " + e.getMessage());
            }
        }
        return cursor;
    }

    /** Applies the custom cursor to the given Scene. */
    public static void apply(Scene scene) {
        ImageCursor c = get();
        if (scene != null && c != null) {
            scene.setCursor(c);
        }
    }

    /** Applies the custom cursor to the given Node (overrides scene cursor for that node). */
    public static void apply(Node node) {
        ImageCursor c = get();
        if (node != null && c != null) {
            node.setCursor(c);
        }
    }

    /**
     * Convenience: adds a sceneProperty listener on {@code anchor} so the cursor
     * is applied as soon as the node is added to a scene.
     */
    public static void applyWhenReady(Node anchor) {
        anchor.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) apply(newScene);
        });
    }
}
