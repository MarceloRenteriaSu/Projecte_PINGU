package VISTAS;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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

import java.sql.Connection;
import java.util.ArrayList;

import GESTORES.GestorBBDD;

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

    private ArrayList<ColorPicker> campColors = new ArrayList<>();
    // Player 1 name field (locked to logged-in user)
    private TextField campNom1 = null;
    // Players 2-4: ComboBox with registered users
    private ArrayList<ComboBox<String>> campsJugadors = new ArrayList<>();

    private String nombreUsuario = "Jugador";
    private Stage menuStage;
    private Connection conexion = null;
    private ArrayList<String> usuarisDisponibles = new ArrayList<>();

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

    public void setConexion(Connection con) {
        this.conexion = con;
        usuarisDisponibles = GestorBBDD.getUsuarios(con, nombreUsuario);
        regenerarCampsNoms();
    }

    @FXML
    private void initialize() {
        // Camp de text per caselles amb valor per defecte
        casillasField.setText("50");

        // Opcions de jugadors
        jugadorsCombo.setItems(FXCollections.observableArrayList(
            "2 jugadores", "3 jugadores", "4 jugadores"
        ));
        jugadorsCombo.setValue("4 jugadores");

        // Foca activada per defecte
        focaCheckBox.setSelected(true);

        // Quan canvia el nombre de jugadors, regenerar els camps de noms
        jugadorsCombo.setOnAction(e -> regenerarCampsNoms());

        feedbackLabel.setText("");
        regenerarCampsNoms();
        CursorManager.applyWhenReady(casillasField);
    }

    /**
     * Regenera els camps de noms segons el nombre de jugadors seleccionat.
     * El jugador 1 és sempre l'usuari logejat (camp bloquejat).
     * Els jugadors 2-4 escullen entre els usuaris registrats via ComboBox.
     */
    private void regenerarCampsNoms() {
        if (nomsContainer == null) return;
        nomsContainer.getChildren().clear();
        campColors.clear();
        campsJugadors.clear();
        campNom1 = null;

        int numJugadors = obtenerNumJugadors();
        for (int i = 0; i < numJugadors; i++) {
            Color defaultColor = DEFAULT_COLORS[i % DEFAULT_COLORS.length];

            ColorPicker cp = new ColorPicker(defaultColor);
            cp.setPrefWidth(140);
            cp.setMinWidth(140);
            cp.setMaxWidth(140);
            cp.getStyleClass().add("button");

            int pw = PinguinoRenderer.COLS * PinguinoRenderer.PREVIEW_PX;
            int ph = PinguinoRenderer.ROWS * PinguinoRenderer.PREVIEW_PX;
            Canvas preview = new Canvas(pw, ph);
            PinguinoRenderer.draw(preview.getGraphicsContext2D(),
                PinguinoRenderer.PREVIEW_PX, defaultColor, false);
            cp.setOnAction(e -> PinguinoRenderer.draw(
                preview.getGraphicsContext2D(),
                PinguinoRenderer.PREVIEW_PX, cp.getValue(), false));

            campColors.add(cp);

            HBox fila;
            if (i == 0) {
                // Player 1: locked to the logged-in user
                TextField tf = new TextField(nombreUsuario);
                tf.setEditable(false);
                tf.setDisable(true);
                tf.getStyleClass().add("field");
                campNom1 = tf;
                fila = new HBox(8, tf, cp, preview);
                HBox.setHgrow(tf, Priority.ALWAYS);
            } else {
                // Players 2+: ComboBox with registered users (excluding logged-in user)
                ComboBox<String> combo = new ComboBox<>(
                    FXCollections.observableArrayList(usuarisDisponibles));
                combo.setPromptText("Selecciona jugador " + (i + 1));
                combo.setMaxWidth(Double.MAX_VALUE);
                combo.getStyleClass().add("cp-combo");
                if (!usuarisDisponibles.isEmpty()) combo.getSelectionModel().selectFirst();
                campsJugadors.add(combo);
                fila = new HBox(8, combo, cp, preview);
                HBox.setHgrow(combo, Priority.ALWAYS);
            }

            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
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

        if (numCasillas < 50 || numCasillas > 150) {
            feedbackLabel.setText("⚠ El número de casillas debe ser entre 50 y 150!");
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Validate all ComboBoxes have a selection
        for (int i = 0; i < campsJugadors.size(); i++) {
            if (campsJugadors.get(i).getValue() == null) {
                feedbackLabel.setText("⚠ Selecciona un usuario para el jugador " + (i + 2) + ".");
                feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }
        }

        // Collect names: player 1 = logged-in user, rest from ComboBoxes
        ArrayList<String> noms = new ArrayList<>();
        noms.add(nombreUsuario);
        for (ComboBox<String> combo : campsJugadors) {
            noms.add(combo.getValue());
        }

        // Validate no duplicates
        for (int i = 0; i < noms.size(); i++) {
            for (int j = i + 1; j < noms.size(); j++) {
                if (noms.get(i).equalsIgnoreCase(noms.get(j))) {
                    feedbackLabel.setText("⚠ ¡No puede haber nombres repetidos!");
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
                    feedbackLabel.setText("⚠ Los jugadores " + (i + 1) + " y " + (j + 1) + " tienen el mismo color!");
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
            String titleFoca = ambFoca ? " (con Foca)" : " (sin Foca)";
            menuStage.setScene(new Scene(root));
            menuStage.setTitle("Juego del Pingüino — " + numJugadors + " jugadores, " + numCasillas + " casillas" + titleFoca);
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
