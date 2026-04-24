package VISTAS;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Dibuja el token-pingüino pixel art en cualquier GraphicsContext.
 * Compartido entre PantallaJuego (tokens pequeños) y PantallaConfig (preview grande).
 */
public final class PinguinoRenderer {

    public static final int COLS = 12;
    public static final int ROWS = 16;

    /** Tamaño de píxel para tokens en el tablero (canvas 24×32). */
    public static final int GAME_PX = 2;

    /** Tamaño de píxel para la vista previa en config (canvas 48×64). */
    public static final int PREVIEW_PX = 4;

    /** Colores de gorro por defecto, uno por jugador. */
    public static final Color[] DEFAULT_COLORS = {
        Color.web("#2f6fed"),
        Color.web("#ef4444"),
        Color.web("#22c55e"),
        Color.web("#facc15")
    };

    /*
     * Leyenda de colores:
     *  0 = transparente
     *  1 = cuerpo negro  (#1a1a2e)
     *  2 = barriga/pompón blanco (#f5f5f5)
     *  3 = gorro (color del jugador)
     *  4 = pico + patas naranja (#ff8c00)
     *  5 = iris del ojo (negro profundo)
     *
     * Grid: 12 columnas × 16 filas — pixelSize escalable.
     */
    private static final int[][] ART = {
        {0,0,0,2,2,2,2,0,0,0,0,0},  //  0  pompón
        {0,0,3,3,3,3,3,3,0,0,0,0},  //  1  gorro alto
        {0,3,3,3,3,3,3,3,3,0,0,0},  //  2  gorro medio
        {3,3,3,3,3,3,3,3,3,3,0,0},  //  3  ala del gorro
        {1,1,1,1,1,1,1,1,1,1,0,0},  //  4  cabeza
        {1,2,5,1,1,1,1,2,5,1,0,0},  //  5  ojos (blanco + iris)
        {1,2,2,1,1,1,1,2,2,1,0,0},  //  6  ojos (segunda fila)
        {1,1,4,4,4,4,1,1,1,1,0,0},  //  7  pico naranja
        {1,1,1,1,1,1,1,1,1,1,0,0},  //  8  cuello
        {1,1,2,2,2,2,2,2,1,1,0,0},  //  9  cuerpo arriba
        {1,2,2,2,2,2,2,2,2,1,0,0},  // 10  cuerpo
        {1,2,2,2,2,2,2,2,2,1,0,0},  // 11  cuerpo
        {1,1,2,2,2,2,2,2,1,1,0,0},  // 12  cuerpo abajo
        {1,1,1,1,1,1,1,1,1,1,0,0},  // 13  base
        {0,4,4,4,0,0,4,4,4,0,0,0},  // 14  patas
        {4,4,0,0,0,0,0,0,4,4,0,0},  // 15  punta de patas
    };

    private PinguinoRenderer() {}

    /**
     * Dibuja el pingüino en el GraphicsContext indicado.
     *
     * @param gc       contexto donde dibujar
     * @param px       tamaño en pantalla de cada "píxel" del arte
     * @param hatColor color del gorro; null → usa azul por defecto
     * @param esFoca   true = esquema de colores de enemigo (gris + gorro rojo)
     */
    public static void draw(GraphicsContext gc, int px, Color hatColor, boolean esFoca) {
        Color bodyC  = esFoca ? Color.web("#263238") : Color.web("#1a1a2e");
        Color bellyC = esFoca ? Color.web("#cfd8dc") : Color.web("#f5f5f5");
        Color irisC  = Color.web("#0a0a14");
        Color beakC  = Color.web("#ff8c00");
        Color hatC   = esFoca ? Color.web("#c62828")
                              : (hatColor != null ? hatColor : DEFAULT_COLORS[0]);

        gc.clearRect(0, 0, COLS * px, ROWS * px);

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
