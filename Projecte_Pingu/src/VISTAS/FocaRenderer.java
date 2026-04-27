package VISTAS;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Dibuja el token-foca pixel art en cualquier GraphicsContext.
 * Usa la misma cuadrícula 12×16 que PinguinoRenderer para mantener
 * coherencia visual en el tablero.
 *
 * Leyenda de colores:
 *  0 = transparente
 *  1 = cuerpo gris oscuro   (#6b7b8d)
 *  2 = barriga gris claro   (#a8b8c8)
 *  3 = nariz / boca negro    (#1a1a2e)
 *  4 = ojos negro            (#0a0a14)
 *  5 = bigotes / detalles    (#3a3a4e)
 *  6 = aleta oscura          (#4a5a6a)
 *  7 = highlight claro       (#c0d0dd)
 */
public final class FocaRenderer {

    /** Reutiliza las mismas dimensiones que PinguinoRenderer. */
    public static final int COLS = PinguinoRenderer.COLS;  // 12
    public static final int ROWS = PinguinoRenderer.ROWS;  // 16

    /*
     * Pixel art de una foca sentada (vista frontal-lateral),
     * inspirada en la imagen de referencia: cuerpecito redondo gris,
     * cabecita con bigotes, aletas a los lados, colita.
     *
     * Grid: 12 columnas × 16 filas
     */
    private static final int[][] ART = {
        //  0  1  2  3  4  5  6  7  8  9 10 11
        {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0},  //  0  cabeza top (ancha)
        {0, 0, 1, 1, 7, 7, 7, 7, 1, 1, 0, 0},  //  1  cabeza con reflejos
        {0, 1, 1, 7, 1, 1, 1, 1, 7, 1, 1, 0},  //  2  cabeza ancha
        {0, 1, 4, 1, 7, 1, 1, 7, 1, 4, 1, 0},  //  3  ojos con cejas
        {0, 1, 1, 1, 1, 3, 3, 1, 1, 1, 1, 0},  //  4  nariz
        {5, 5, 1, 1, 3, 2, 2, 3, 1, 1, 5, 5},  //  5  boca + bigotes (largos)
        {0, 5, 0, 1, 1, 2, 2, 1, 1, 0, 5, 0},  //  6  bigotes + mentón
        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},  //  7  cuello (ancho)
        {0, 6, 1, 1, 2, 2, 2, 2, 1, 1, 6, 0},  //  8  cuerpo + aletas
        {6, 6, 1, 2, 2, 2, 2, 2, 2, 1, 6, 6},  //  9  cuerpo gordo + aletas
        {0, 6, 1, 2, 2, 2, 2, 2, 2, 1, 6, 0},  // 10  cuerpo + aletas punta
        {0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0},  // 11  barriga ancha
        {0, 0, 1, 1, 2, 2, 2, 2, 1, 1, 0, 0},  // 12  barriga baja
        {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0},  // 13  base cuerpo
        {0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0},  // 14  aletas traseras
        {0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0},  // 15  punta aletas
    };

    private FocaRenderer() {}

    /**
     * Dibuja la foca en el GraphicsContext indicado.
     *
     * @param gc contexto donde dibujar
     * @param px tamaño en pantalla de cada "píxel" del arte
     */
    public static void draw(GraphicsContext gc, int px) {
        Color bodyC      = Color.web("#6b7b8d");   // cuerpo gris
        Color bellyC     = Color.web("#a8b8c8");   // barriga clara
        Color noseC      = Color.web("#1a1a2e");   // nariz/boca
        Color eyeC       = Color.web("#0a0a14");   // ojos
        Color whiskerC   = Color.web("#3a3a4e");   // bigotes
        Color flipperC   = Color.web("#4a5a6a");   // aletas
        Color highlightC = Color.web("#c0d0dd");   // reflejos

        gc.clearRect(0, 0, COLS * px, ROWS * px);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Color fill;
                switch (ART[r][c]) {
                    case 1: fill = bodyC;      break;
                    case 2: fill = bellyC;     break;
                    case 3: fill = noseC;      break;
                    case 4: fill = eyeC;       break;
                    case 5: fill = whiskerC;   break;
                    case 6: fill = flipperC;   break;
                    case 7: fill = highlightC; break;
                    default: continue;
                }
                gc.setFill(fill);
                gc.fillRect(c * px, r * px, px, px);
            }
        }
    }
}
