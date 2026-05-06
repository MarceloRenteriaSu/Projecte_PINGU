package VISTAS;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
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

    /** Stores the currently selected Color for each player. */
    private ArrayList<Color> campSelectedColors = new ArrayList<>();
    /** Stores the color-selector buttons so we can update their appearance. */
    private ArrayList<Button> campColorButtons = new ArrayList<>();
    // Player 1 name field (locked to logged-in user)
    private TextField campNom1 = null;
    // Players 2-4: ComboBox with registered users
    private ArrayList<ComboBox<String>> campsJugadors = new ArrayList<>();

    private String nombreUsuario = "Jugador";
    private Stage menuStage;
    private Connection conexion = null;
    private ArrayList<String> usuarisDisponibles = new ArrayList<>();

    /**
     * 16 maximally distinct colours — each one is a unique hue,
     * no two colours share a similar shade or scale.
     */
    private static final Color[] PALETTE = {
        /* Row 1 */
        Color.web("#2563EB"),   // Blue
        Color.web("#DC2626"),   // Red
        Color.web("#16A34A"),   // Green
        Color.web("#EAB308"),   // Yellow
        /* Row 2 */
        Color.web("#9333EA"),   // Purple
        Color.web("#EA580C"),   // Orange
        Color.web("#EC4899"),   // Pink
        Color.web("#0891B2"),   // Cyan
        /* Row 3 */
        Color.web("#854D0E"),   // Brown
        Color.web("#000000"),   // Black
        Color.web("#6B7280"),   // Gray
        Color.web("#65A30D"),   // Lime
        /* Row 4 */
        Color.web("#BE185D"),   // Magenta
        Color.web("#1D4ED8"),   // Navy
        Color.web("#0F766E"),   // Teal
        Color.web("#7E22CE"),   // Violet
    };

    private static final int PALETTE_COLS = 4;

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

    /* ------------------------------------------------------------------ */
    /*  Custom colour-palette popup (replaces ColorPicker)                 */
    /* ------------------------------------------------------------------ */

    /**
     * Converts a Color to its hex #RRGGBB string.
     */
    private static String colorToHex(Color c) {
        return String.format("#%02X%02X%02X",
            (int) Math.round(c.getRed()   * 255),
            (int) Math.round(c.getGreen() * 255),
            (int) Math.round(c.getBlue()  * 255));
    }

    /**
     * Creates a styled Button that displays the given colour and opens
     * a 20-colour popup palette when clicked.
     */
    private Button createColorButton(Color initial, int playerIndex, Canvas preview) {
        Button btn = new Button();
        btn.setPrefSize(36, 36);
        btn.setMinSize(36, 36);
        btn.setMaxSize(36, 36);
        btn.setStyle(colorButtonStyle(initial));
        btn.setCursor(javafx.scene.Cursor.HAND);

        btn.setOnAction(e -> showColorPopup(btn, playerIndex, preview));
        return btn;
    }

    /**
     * Returns inline CSS for a colour-swatch button.
     */
    private static String colorButtonStyle(Color c) {
        String hex = colorToHex(c);
        return "-fx-background-color: " + hex + ";"
             + "-fx-background-radius: 6;"
             + "-fx-border-color: white;"
             + "-fx-border-width: 2.5;"
             + "-fx-border-radius: 6;"
             + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 4, 0.3, 0, 2);"
             + "-fx-cursor: hand;";
    }

    /**
     * Shows a Popup anchored to {@code owner} with a 5×4 grid of colour swatches.
     */
    private void showColorPopup(Button owner, int playerIndex, Canvas preview) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(10));
        grid.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #daeffc, #aedef5);"
          + "-fx-background-radius: 10;"
          + "-fx-border-color: white;"
          + "-fx-border-width: 3;"
          + "-fx-border-radius: 10;"
          + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 14, 0.25, 0, 5);"
        );

        Color currentColor = campSelectedColors.get(playerIndex);

        for (int idx = 0; idx < PALETTE.length; idx++) {
            Color palColor = PALETTE[idx];
            int col = idx % PALETTE_COLS;
            int row = idx / PALETTE_COLS;

            StackPane cell = new StackPane();
            Rectangle rect = new Rectangle(32, 32);
            rect.setArcWidth(8);
            rect.setArcHeight(8);
            rect.setFill(palColor);
            rect.setStroke(Color.WHITE);
            rect.setStrokeWidth(2);

            // Highlight the currently selected colour
            if (palColor.equals(currentColor)) {
                rect.setStroke(Color.web("#0d4680"));
                rect.setStrokeWidth(3);
            }

            cell.getChildren().add(rect);
            cell.setCursor(javafx.scene.Cursor.HAND);

            // Hover effect
            cell.setOnMouseEntered(ev -> {
                rect.setScaleX(1.18);
                rect.setScaleY(1.18);
            });
            cell.setOnMouseExited(ev -> {
                rect.setScaleX(1.0);
                rect.setScaleY(1.0);
            });

            final Color chosen = palColor;
            cell.setOnMouseClicked(ev -> {
                campSelectedColors.set(playerIndex, chosen);
                owner.setStyle(colorButtonStyle(chosen));
                PinguinoRenderer.draw(
                    preview.getGraphicsContext2D(),
                    PinguinoRenderer.PREVIEW_PX, chosen, false);
                popup.hide();
            });

            grid.add(cell, col, row);
        }

        popup.getContent().add(grid);

        // Position the popup just below the button
        javafx.geometry.Bounds screenBounds = owner.localToScreen(owner.getBoundsInLocal());
        popup.show(owner, screenBounds.getMinX(), screenBounds.getMaxY() + 4);
    }

    /* ------------------------------------------------------------------ */

    /**
     * Regenera els camps de noms segons el nombre de jugadors seleccionat.
     * El jugador 1 és sempre l'usuari logejat (camp bloquejat).
     * Els jugadors 2-4 escullen entre els usuaris registrats via ComboBox.
     */
    private void regenerarCampsNoms() {
        if (nomsContainer == null) return;
        nomsContainer.getChildren().clear();
        campSelectedColors.clear();
        campColorButtons.clear();
        campsJugadors.clear();
        campNom1 = null;

        int numJugadors = obtenerNumJugadors();
        for (int i = 0; i < numJugadors; i++) {
            Color defaultColor = PALETTE[i % PALETTE.length];
            campSelectedColors.add(defaultColor);

            int pw = PinguinoRenderer.COLS * PinguinoRenderer.PREVIEW_PX;
            int ph = PinguinoRenderer.ROWS * PinguinoRenderer.PREVIEW_PX;
            Canvas preview = new Canvas(pw, ph);
            PinguinoRenderer.draw(preview.getGraphicsContext2D(),
                PinguinoRenderer.PREVIEW_PX, defaultColor, false);

            Button colorBtn = createColorButton(defaultColor, i, preview);
            campColorButtons.add(colorBtn);

            HBox fila;
            if (i == 0) {
                // Player 1: locked to the logged-in user
                TextField tf = new TextField(nombreUsuario);
                tf.setEditable(false);
                tf.setDisable(true);
                tf.getStyleClass().add("field");
                campNom1 = tf;
                fila = new HBox(8, tf, colorBtn, preview);
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
                fila = new HBox(8, combo, colorBtn, preview);
                HBox.setHgrow(combo, Priority.ALWAYS);
            }

            fila.setAlignment(Pos.CENTER_LEFT);
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
        for (Color c : campSelectedColors) {
            colors.add(colorToHex(c));
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
