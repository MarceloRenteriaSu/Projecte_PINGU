package VISTAS;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.ArrayList;

/**
 * Pantalla de configuració: nombre de caselles, nombre de jugadors, noms i foca.
 * Es mostra després del login i abans d'iniciar la partida.
 */
public class PantallaConfig {

    @FXML private TextField casillasField;
    @FXML private ComboBox<String> jugadorsCombo;
    @FXML private CheckBox focaCheckBox;
    @FXML private VBox nomsContainer;
    @FXML private Label feedbackLabel;
    @FXML private Button btnComenzar;

    private ArrayList<TextField> campsNoms = new ArrayList<>();
    private String nombreUsuario = "Jugador";

    public void setNombreUsuario(String nom) {
        this.nombreUsuario = nom;
    }

    @FXML
    private void initialize() {
        // Camp de text per caselles amb valor per defecte
        casillasField.setText("50");

        // Opcions de jugadors
        jugadorsCombo.setItems(FXCollections.observableArrayList(
            "2 jugadors", "3 jugadors", "4 jugadors"
        ));
        jugadorsCombo.setValue("4 jugadors");

        // Foca activada per defecte
        focaCheckBox.setSelected(true);

        // Quan canvia el nombre de jugadors, regenerar els camps de noms
        jugadorsCombo.setOnAction(e -> regenerarCampsNoms());

        feedbackLabel.setText("");
        regenerarCampsNoms();
    }

    /**
     * Regenera els camps de text per als noms dels jugadors
     * segons el nombre seleccionat al ComboBox.
     */
    private void regenerarCampsNoms() {
        nomsContainer.getChildren().clear();
        campsNoms.clear();

        int numJugadors = obtenerNumJugadors();
        for (int i = 0; i < numJugadors; i++) {
            TextField tf = new TextField();
            tf.setPromptText("Nom del Jugador " + (i + 1));
            tf.getStyleClass().add("field");
            // Primer camp: posar el nom del login
            if (i == 0) {
                tf.setText(nombreUsuario);
            }
            campsNoms.add(tf);
            nomsContainer.getChildren().add(tf);
        }
    }

    private int obtenerNumJugadors() {
        String sel = jugadorsCombo.getValue();
        if (sel == null) return 4;
        if (sel.startsWith("2")) return 2;
        if (sel.startsWith("3")) return 3;
        return 4;
    }

    private int obtenerNumCasillas() {
        String text = casillasField.getText().trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 50;
        }
    }

    @FXML
    private void handleComenzar(ActionEvent event) {
        int numJugadors = obtenerNumJugadors();
        int numCasillas = obtenerNumCasillas();
        boolean ambFoca = focaCheckBox.isSelected();

        // Validar rang de caselles (50 a 150)
        if (numCasillas < 50 || numCasillas > 150) {
            feedbackLabel.setText("⚠ El nombre de caselles ha de ser entre 50 i 150!");
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Recollir noms
        ArrayList<String> noms = new ArrayList<>();
        for (int i = 0; i < campsNoms.size(); i++) {
            String nom = campsNoms.get(i).getText().trim();
            if (nom.isEmpty()) {
                nom = "Jugador" + (i + 1);
            }
            noms.add(nom);
        }

        // Validar que no hi hagi noms repetits
        for (int i = 0; i < noms.size(); i++) {
            for (int j = i + 1; j < noms.size(); j++) {
                if (noms.get(i).equalsIgnoreCase(noms.get(j))) {
                    feedbackLabel.setText("⚠ No pot haver-hi noms repetits!");
                    feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
                    return;
                }
            }
        }

        // Obrir PantallaJuego amb els paràmetres
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
            Parent root = loader.load();

            PantallaJuego ctrl = loader.getController();
            ctrl.setNombreUsuario(nombreUsuario);
            ctrl.iniciarJoc(numCasillas, noms, ambFoca);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            String titleFoca = ambFoca ? " (amb Foca)" : " (sense Foca)";
            stage.setTitle("Joc del Pingüí — " + numJugadors + " jugadors, " + numCasillas + " caselles" + titleFoca);
        } catch (Exception e) {
            e.printStackTrace();
            feedbackLabel.setText("❌ Error: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }
}
