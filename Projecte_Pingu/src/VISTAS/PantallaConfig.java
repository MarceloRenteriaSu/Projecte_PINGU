package VISTAS;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private ArrayList<ColorPicker> campColors = new ArrayList<>();
    private String nombreUsuario = "Jugador";
    private Stage menuStage;

    // Default colors matching PantallaJuego.css (#P1–#P4)
    private static final Color[] DEFAULT_COLORS = {
        Color.web("#2f6fed"),
        Color.web("#ef4444"),
        Color.web("#22c55e"),
        Color.web("#facc15")
    };

    public void setNombreUsuario(String nom) {
        this.nombreUsuario = nom;
    }

    public void setMenuStage(Stage stage) {
        this.menuStage = stage;
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
        campColors.clear();

        int numJugadors = obtenerNumJugadors();
        for (int i = 0; i < numJugadors; i++) {
            TextField tf = new TextField();
            tf.setPromptText("Nom del Jugador " + (i + 1));
            tf.getStyleClass().add("field");
            if (i == 0) {
                tf.setText(nombreUsuario);
            }

            ColorPicker cp = new ColorPicker(DEFAULT_COLORS[i % DEFAULT_COLORS.length]);
            cp.setPrefWidth(90);
            cp.setMinWidth(90);
            cp.setMaxWidth(90);
            cp.getStyleClass().add("button");

            HBox fila = new HBox(8, tf, cp);
            HBox.setHgrow(tf, Priority.ALWAYS);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            campsNoms.add(tf);
            campColors.add(cp);
            nomsContainer.getChildren().add(fila);
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

        // Recollir colors dels jugadors
        ArrayList<String> colors = new ArrayList<>();
        for (ColorPicker cp : campColors) {
            Color c = cp.getValue();
            colors.add(String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed()   * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue()  * 255)));
        }

        // Validar que no hi hagi colors repetits
        for (int i = 0; i < colors.size(); i++) {
            for (int j = i + 1; j < colors.size(); j++) {
                if (colors.get(i).equalsIgnoreCase(colors.get(j))) {
                    feedbackLabel.setText("⚠ Els jugadors " + (i + 1) + " i " + (j + 1) + " tenen el mateix color!");
                    feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
                    return;
                }
            }
        }

        // Obrir PantallaJuego en el Stage del menú i tancar la finestra de config
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
            Parent root = loader.load();

            PantallaJuego ctrl = loader.getController();
            ctrl.setNombreUsuario(nombreUsuario);
            ctrl.iniciarJoc(numCasillas, noms, colors, ambFoca);

            Stage configStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            String titleFoca = ambFoca ? " (amb Foca)" : " (sense Foca)";
            menuStage.setScene(new Scene(root));
            menuStage.setTitle("Joc del Pingüí — " + numJugadors + " jugadors, " + numCasillas + " caselles" + titleFoca);
            configStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            feedbackLabel.setText("❌ Error: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        Stage configStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        configStage.close();
    }
}
