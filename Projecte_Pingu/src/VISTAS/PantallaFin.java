package VISTAS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla de fi de partida.
 * Mostra el guanyador i el nombre de torns.
 */
public class PantallaFin {

    @FXML private Text icona;
    @FXML private Text titulo;
    @FXML private Text guanyador;
    @FXML private Text statsText;
    @FXML private Button btnMenu;
    @FXML private Button btnTancar;

    private boolean volverAlMenu = false;

    /**
     * Inicialitza la pantalla amb les dades del resultat.
     */
    public void inicialitzar(String nomGuanyador, int turnos) {
        icona.setText("🏆");
        titulo.setText("Fi del Joc!");
        guanyador.setText("Guanyador: " + nomGuanyador);
        statsText.setText("Torns jugats: " + turnos);
    }

    /**
     * Overload per compatibilitat amb crides antigues (ignorant dificultat).
     */
    public void inicialitzar(String nomGuanyador, int turnos, String dificultat) {
        inicialitzar(nomGuanyador, turnos);
    }

    public boolean isVolverAlMenu() {
        return volverAlMenu;
    }

    @FXML
    private void handleVolverMenu() {
        volverAlMenu = true;
        Stage stage = (Stage) btnMenu.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleTancar() {
        volverAlMenu = false;
        Stage stage = (Stage) btnTancar.getScene().getWindow();
        stage.close();
    }
}
