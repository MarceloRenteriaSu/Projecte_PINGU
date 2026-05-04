package VISTAS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import MODELOS.*;

/**
 * Controlador de la pantalla de guerra de neu.
 *
 * Qui obre aquesta pantalla: PantallaJuego, quan el jugador que ACABA
 * de moure's coincideix amb un pingüí que JA estava a la casella.
 *
 * El jugador que JA estava (defensor) és qui pren la decisió:
 *   - Tirar el dau i escapar (es mourà els passos resultants al tauler principal).
 *   - Entrar en batalla de neu (guanya qui té més boles, el perdedor retrocedeix
 *     la diferència; empat → ningú es mou; tots dos perden totes les boles).
 */
public class PantallaGuerra {

    @FXML private Text titulo;
    @FXML private Text infoDefensor;
    @FXML private Text infoAtacant;
    @FXML private Text pregunta;
    @FXML private Text resultat;
    @FXML private Button btnDau;
    @FXML private Button btnBatalla;
    @FXML private Button btnTancar;

    // Jugadors implicats
    private Pinguino defensor;   // el que JA estava a la casella (el que tria)
    private Pinguino atacant;    // el que ACABA d'arribar a la casella
    private Partida  partida;

    // Callback → PantallaJuego necessita saber el resultat per actualitzar la UI
    private GuerraCallback callback;

    @FXML
    private void initialize() {
        CursorManager.applyWhenReady(btnDau);
    }

    /**
     * Interfície de retorn cap a PantallaJuego.
     */
    public interface GuerraCallback {
        /**
         * @param defensorRetrocedeix caselles que ha de retrocedir el defensor (0 si no)
         * @param atacantRetrocedeix  caselles que ha de retrocedir l'atacant  (0 si no)
         * @param passosDau           passos del dau si el defensor ha triat escapar (0 si batalla)
         * @param haEscapat           true si el defensor ha triat tirar el dau
         */
        void onGuerraFinalitzada(int defensorRetrocedeix, int atacantRetrocedeix,
                                 int passosDau, boolean haEscapat);
    }

    // -------------------------------------------------------
    // INICIALITZACIÓ
    // -------------------------------------------------------

    /**
     * Ha de cridar-se des de PantallaJuego JUST ABANS de mostrar la finestra.
     */
    public void inicialitzar(Pinguino defensor, Pinguino atacant,
                             Partida partida, GuerraCallback callback) {
        this.defensor  = defensor;
        this.atacant   = atacant;
        this.partida   = partida;
        this.callback  = callback;

        int bolesD = defensor.getInv().contarItem(new Bola(0));
        int bolesA = atacant.getInv().contarItem(new Bola(0));

        infoDefensor.setText("🐧 " + defensor.getNom() + " (defensor) — ❄ " + bolesD + " bolas de nieve");
        infoAtacant.setText("🐧 " + atacant.getNom()   + " (atacante)");
        pregunta.setText(defensor.getNom() + ", ¿pruebas la suerte o entras en guerra?");

        // Si el defensor no té boles, no pot batallar
        btnBatalla.setDisable(bolesD == 0 && bolesA == 0);
    }

    // -------------------------------------------------------
    // HANDLERS
    // -------------------------------------------------------

    /**
     * El defensor tria tirar el dau i escapar de la casella.
     * El resultat (passosDau) el gestionarà PantallaJuego.
     */
    @FXML
    private void handleTirarDau() {
        Dado d = new Dado("Normal", 1);
        int tirada = d.tirar();

        resultat.setText(defensor.getNom() + " tira el dado → " + tirada
                + " casillas! Escapa de la batalla.");
        mostrarResultatFinal();

        // Avisar PantallaJuego: el defensor escapa, cap retrocés per ningú
        if (callback != null) {
            callback.onGuerraFinalitzada(0, 0, tirada, true);
        }
    }

    /**
     * El defensor tria la batalla de neu.
     * Regles:
     *   - Guanya qui té més boles → el perdedor retrocedeix la diferència de caselles.
     *   - Empat → ningú retrocedeix.
     *   - Tots dos perden TOTES les seves boles.
     */
    @FXML
    private void handleBatalla() {
        int bolesD = defensor.getInv().contarItem(new Bola(0));
        int bolesA = atacant.getInv().contarItem(new Bola(0));

        // Tots dos perden totes les boles
        defensor.quitarItem(new Bola(0));
        atacant.quitarItem(new Bola(0));

        int defensorRetrocedeix = 0;
        int atacantRetrocedeix  = 0;
        String msg;

        if (bolesD > bolesA) {
            int diff = bolesD - bolesA;
            atacantRetrocedeix = diff;
            msg = "❄ " + defensor.getNom() + " ¡gana! "
                + atacant.getNom() + " retrocede " + diff + " casillas.";
        } else if (bolesA > bolesD) {
            int diff = bolesA - bolesD;
            defensorRetrocedeix = diff;
            msg = "❄ " + atacant.getNom() + " ¡gana! "
                + defensor.getNom() + " retrocede " + diff + " casillas.";
        } else {
            msg = "❄ ¡Empate! Nadie retrocede. Ambos pierden las bolas.";
        }

        resultat.setText(msg);
        mostrarResultatFinal();

        if (callback != null) {
            callback.onGuerraFinalitzada(defensorRetrocedeix, atacantRetrocedeix, 0, false);
        }
    }

    /** Amaga els botons d'acció i mostra el botó de continuar. */
    private void mostrarResultatFinal() {
        btnDau.setDisable(true);
        btnBatalla.setDisable(true);
        btnTancar.setVisible(true);
    }

    /** Tanca la finestra de guerra. */
    @FXML
    private void handleTancar() {
        Stage stage = (Stage) btnTancar.getScene().getWindow();
        stage.close();
    }
}
