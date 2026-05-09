package VISTAS;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import GESTORES.GestorBBDD;

/**
 * Pantalla de selecció de partida guardada.
 * Mostra totes les partides de l'usuari i permet carregar-ne o esborrar-ne.
 */
public class PantallaCargarPartida {

    @FXML private TableView<PartidaItem> tablePartides;
    @FXML private TableColumn<PartidaItem, String> colNom;
    @FXML private TableColumn<PartidaItem, String> colJugadors;
    @FXML private TableColumn<PartidaItem, String> colTorns;
    @FXML private TableColumn<PartidaItem, String> colData;
    @FXML private Button btnCarregar;
    @FXML private Button btnEsborrar;
    @FXML private Button btnTornar;
    @FXML private Label lblUsername;

    private Connection conexion;
    private String username;

    // Set to non-null when user clicks "Carregar"
    private LinkedHashMap<String, String> selectedPartida = null;
    private boolean loaded = false;

    // -------------------------------------------------------
    // FXML INIT
    // -------------------------------------------------------

    @FXML
    private void initialize() {
        // Wire columns using lambda cell factories (works with any POJO)
        colNom.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getNomPartida()));
        colJugadors.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getJugadors()));
        colTorns.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getTorns()));
        colData.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getData()));

        // Style the table header and rows
        tablePartides.setStyle(
            "-fx-background-color: #2a2a4e;" +
            "-fx-control-inner-background: #2a2a4e;" +
            "-fx-text-fill: white;");
        CursorManager.applyWhenReady(tablePartides);
        CursorManager.applyClickable(btnCarregar);
        CursorManager.applyClickable(btnEsborrar);
        CursorManager.applyClickable(btnTornar);
    }

    // -------------------------------------------------------
    // SETUP — called by the screen that opens this dialog
    // -------------------------------------------------------

    /**
     * Inicialitza la pantalla amb la connexió i el nom d'usuari.
     * Ha de ser cridat just després de carregar el FXML.
     */
    public void inicialitzar(Connection con, String username) {
        this.conexion = con;
        this.username = username;
        if (lblUsername != null) {
            lblUsername.setText("Jugador: " + username);
        }
        carregarLlista();
    }

    // -------------------------------------------------------
    // LOAD LIST FROM DB
    // -------------------------------------------------------

    private void carregarLlista() {
        ArrayList<LinkedHashMap<String, String>> llista =
            GestorBBDD.listarPartidas(conexion, username);

        ObservableList<PartidaItem> items = FXCollections.observableArrayList();
        for (LinkedHashMap<String, String> m : llista) {
            String idStr    = m.getOrDefault("ID", "0");
            String nom      = m.getOrDefault("NOM_PARTIDA", "Partida");
            String jugadors = m.getOrDefault("NOMBRES_JUGADORS", "-");
            String torns    = m.getOrDefault("TURNOS", "0");
            String data     = m.getOrDefault("FECHA_GUARDADO", "-");

            // Trim timestamp to readable length
            if (data != null && data.length() > 16) {
                data = data.substring(0, 16);
            }

            items.add(new PartidaItem(Integer.parseInt(idStr), nom, jugadors, torns, data));
        }
        tablePartides.setItems(items);
    }

    // -------------------------------------------------------
    // BUTTON HANDLERS
    // -------------------------------------------------------

    @FXML
    private void handleCarregar(ActionEvent event) {
        PartidaItem sel = tablePartides.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Selecciona una partida para cargar.");
        } else {
            LinkedHashMap<String, String> datos = GestorBBDD.cargarPartidaPorId(conexion, sel.getId());
            if (datos != null) {
                selectedPartida = datos;
                loaded = true;
                tancarStage(event);
            } else {
                mostrarAlerta("Error", "No se ha podido cargar la partida de la base de datos.");
            }
        }
    }

    @FXML
    private void handleEsborrar(ActionEvent event) {
        PartidaItem sel = tablePartides.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Selecciona una partida para borrar.");
        } else {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Seguro que quieres borrar la partida '" + sel.getNomPartida() + "'?\nEsta acción no se puede deshacer.",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                boolean ok = GestorBBDD.borrarPartidaPorId(conexion, sel.getId());
                if (ok) {
                    carregarLlista(); // Refresh the table
                } else {
                    mostrarAlerta("Error", "No se ha podido borrar la partida.");
                }
            }
        });
        }
    }

    @FXML
    private void handleTornar(ActionEvent event) {
        tancarStage(event);
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    private void tancarStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // -------------------------------------------------------
    // RESULT GETTERS (checked by the caller after showAndWait)
    // -------------------------------------------------------

    /** @return true if the user selected and confirmed a match to load */
    public boolean isLoaded() {
        return loaded;
    }

    /** @return the full DB row of the selected match, or null if none was chosen */
    public LinkedHashMap<String, String> getSelectedPartida() {
        return selectedPartida;
    }

    // -------------------------------------------------------
    // TABLE ROW MODEL
    // -------------------------------------------------------

    /** Immutable data model for one row in the saved-games TableView. */
    public static class PartidaItem {
        private final int    id;
        private final String nomPartida;
        private final String jugadors;
        private final String torns;
        private final String data;

        public PartidaItem(int id, String nomPartida, String jugadors, String torns, String data) {
            this.id         = id;
            this.nomPartida = nomPartida != null ? nomPartida : "Partida";
            this.jugadors   = jugadors   != null ? jugadors   : "-";
            this.torns      = torns      != null ? torns      : "0";
            this.data       = data       != null ? data       : "-";
        }

        public int    getId()         { return id; }
        public String getNomPartida() { return nomPartida; }
        public String getJugadors()   { return jugadors; }
        public String getTorns()      { return torns; }
        public String getData()       { return data; }
    }
}
