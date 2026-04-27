package VISTAS;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Draws the player token and seal token.
 * Uses PINGU_YELLOW_IDLE.png (beanie tinted per player) and FOCA_1.png.
 * Falls back to pixel-art if PNGs are missing.
 */
public final class PinguinoRenderer {

    public static final int COLS = 12;
    public static final int ROWS = 16;

    /** Pixel size for game-board tokens (canvas 32×40). */
    public static final int GAME_PX = 2;

    /** Pixel size for config preview canvas (canvas 72×96). */
    public static final int PREVIEW_PX = 6;

    public static final Color[] DEFAULT_COLORS = {
        Color.web("#2f6fed"),
        Color.web("#ef4444"),
        Color.web("#22c55e"),
        Color.web("#facc15")
    };

    private static Image pinguBase;
    private static Image focaBase;

    private PinguinoRenderer() {}

    private static Image getPinguBase() {
        if (pinguBase == null) {
            try {
                pinguBase = new Image(PinguinoRenderer.class
                    .getResourceAsStream("/pngs_fichas/PINGU_YELLOW_IDLE.png"));
            } catch (Exception ignored) {}
        }
        return pinguBase;
    }

    private static Image getFocaBase() {
        if (focaBase == null) {
            try {
                focaBase = new Image(PinguinoRenderer.class
                    .getResourceAsStream("/pngs_fichas/FOCA_1.png"));
            } catch (Exception ignored) {}
        }
        return focaBase;
    }

    /**
     * Draws the token into gc, scaling to fit the canvas size.
     *
     * @param gc       target GraphicsContext
     * @param px       pixel size for fallback pixel-art
     * @param hatColor player colour (tints the yellow beanie); null → default blue
     * @param esFoca   true → draw seal sprite instead of penguin
     */
    public static void draw(GraphicsContext gc, int px, Color hatColor, boolean esFoca) {
        double w = gc.getCanvas().getWidth();
        double h = gc.getCanvas().getHeight();
        if (w <= 0) w = COLS * px;
        if (h <= 0) h = ROWS * px;
        gc.clearRect(0, 0, w, h);

        if (esFoca) {
            Image img = getFocaBase();
            if (img != null && !img.isError()) {
                gc.drawImage(img, 0, 0, w, h);
                return;
            }
            drawFallback(gc, px, null, true);
        } else {
            Image base = getPinguBase();
            if (base != null && !base.isError()) {
                Color hat = hatColor != null ? hatColor : DEFAULT_COLORS[0];
                gc.drawImage(tintYellow(base, hat), 0, 0, w, h);
                return;
            }
            drawFallback(gc, px, hatColor, false);
        }
    }

    /** Replaces red pixels (hat + scarf) across the whole image with target colour. */
    private static WritableImage tintYellow(Image source, Color target) {
        int sw = (int) source.getWidth();
        int sh = (int) source.getHeight();
        WritableImage out = new WritableImage(sw, sh);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = out.getPixelWriter();
        double tgtB = target.getBrightness();

        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                Color c = reader.getColor(x, y);
                if (c.getOpacity() >= 0.05 && isHatColor(c)) {
                    double scale = tgtB > 0.01
                        ? Math.min(c.getBrightness() / tgtB, 2.5)
                        : c.getBrightness();
                    writer.setColor(x, y, target.deriveColor(0, 1.0, scale, c.getOpacity()));
                } else {
                    writer.setColor(x, y, c);
                }
            }
        }
        return out;
    }

    /** Returns true if the pixel matches the red hat colour. */
    private static boolean isHatColor(Color c) {
        double h = c.getHue();
        // Red wraps around 0°: covers 0–20° and 340–360°
        boolean isRed = (h <= 20 || h >= 340);
        return isRed && c.getSaturation() > 0.45 && c.getBrightness() > 0.30;
    }

    // ── Fallback pixel-art (used when PNG files are not found) ────────────

    private static final int[][] ART = {
        {0,0,0,2,2,2,2,0,0,0,0,0},
        {0,0,3,3,3,3,3,3,0,0,0,0},
        {0,3,3,3,3,3,3,3,3,0,0,0},
        {3,3,3,3,3,3,3,3,3,3,0,0},
        {1,1,1,1,1,1,1,1,1,1,0,0},
        {1,2,5,1,1,1,1,2,5,1,0,0},
        {1,2,2,1,1,1,1,2,2,1,0,0},
        {1,1,4,4,4,4,1,1,1,1,0,0},
        {1,1,1,1,1,1,1,1,1,1,0,0},
        {1,1,2,2,2,2,2,2,1,1,0,0},
        {1,2,2,2,2,2,2,2,2,1,0,0},
        {1,2,2,2,2,2,2,2,2,1,0,0},
        {1,1,2,2,2,2,2,2,1,1,0,0},
        {1,1,1,1,1,1,1,1,1,1,0,0},
        {0,4,4,4,0,0,4,4,4,0,0,0},
        {4,4,0,0,0,0,0,0,4,4,0,0},
    };

    private static void drawFallback(GraphicsContext gc, int px, Color hatColor, boolean esFoca) {
        Color bodyC  = esFoca ? Color.web("#263238") : Color.web("#1a1a2e");
        Color bellyC = esFoca ? Color.web("#cfd8dc") : Color.web("#f5f5f5");
        Color irisC  = Color.web("#0a0a14");
        Color beakC  = Color.web("#ff8c00");
        Color hatC   = esFoca ? Color.web("#c62828")
                              : (hatColor != null ? hatColor : DEFAULT_COLORS[0]);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Color fill;
                switch (ART[r][c]) {
                    case 1: fill = bodyC;  break;
                    case 2: fill = bellyC; break;
                    case 3: fill = hatC;   break;
                    case 4: fill = beakC;  break;
                    case 5: fill = irisC;  break;
                    default: continue;
                }
                gc.setFill(fill);
                gc.fillRect(c * px, r * px, px, px);
            }
        }
    }
}
