package VISTAS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import GESTORES.GestorBBDD;

public class PantallaMenu {

    @FXML private Button btnNewMatch;
    @FXML private Button btnLoadMatch;
    @FXML private Button btnCredits;
    @FXML private Button btnExit;
    @FXML private ImageView bgImageView;
    @FXML private Label usernameLabel;

    // Connexió a la BBDD
    private Connection conexion = null;
    private String nombreUsuarioLogueado = "guest";

    public void setNombreUsuario(String username) {
        this.nombreUsuarioLogueado = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Jugador: " + username);
        }
    }

    @FXML
    private void initialize() {
        System.out.println("PantallaMenu initialized");

        // Bind background image to always fill the parent StackPane
        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane parent = (StackPane) bgImageView.getParent();
                bgImageView.fitWidthProperty().bind(parent.widthProperty());
                bgImageView.fitHeightProperty().bind(parent.heightProperty());
            }
        });

        // Connectar a la BBDD amb les credencials hardcodeades
        try {
            conexion = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
            System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewMatch(ActionEvent event) {
        System.out.println("New Match clicked");
        abrirPantallaConfig(event, nombreUsuarioLogueado);
    }

    @FXML
    private void handleLoadMatch(ActionEvent event) {
        System.out.println("Load Match clicked");
        if (conexion == null) {
        	mostrarAlerta("Error", "No hi ha connexió a la base de dades.");
        	return;
        }
        
        // Simular que busquem la partida de l'usuari
        String sql = "SELECT * FROM PINGU_PARTIDAS WHERE USERNAME = '" + nombreUsuarioLogueado + "'";
        try {
            // Això fallarà si la taula PINGU_PARTIDAS no existeix, però simulem que comprovem
            java.util.ArrayList<java.util.LinkedHashMap<String, String>> res = GestorBBDD.select(conexion, sql);
            if (res != null && !res.isEmpty()) {
                // Existeix
                System.out.println("Partida trobada per " + nombreUsuarioLogueado);
                try {
		            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
		            Parent root = loader.load();
		            PantallaJuego ctrl = loader.getController();
		            ctrl.setNombreUsuario(nombreUsuarioLogueado);
		            ctrl.iniciarJoc(); // O el que pertoqui per carregar
		            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		            stage.setScene(new Scene(root));
		            stage.setTitle("El Joc del Pingu");
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
            } else {
                mostrarAlerta("Informació", "No tens cap partida guardada en curs.");
            }
        } catch (Exception e) {
        	// Error, per exemple no existeix la taula encara
        	mostrarAlerta("Error", "No s'ha pogut verificar a la base de dades: " + e.getMessage());
        }
    }

    @FXML
    private void handleCredits(ActionEvent event) {
        mostrarAlerta("Crèdits", "Creadors del joc:\n- ACOM\n- DW2526 GR02");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        if (conexion != null) {
            GestorBBDD.cerrar(conexion);
        }
        System.exit(0);
    }

    /**
     * Obre la pantalla de configuració (noms de jugadors i caselles).
     */
    private void abrirPantallaConfig(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaConfig.fxml"));
            Parent pantallaConfigRoot = loader.load();

            PantallaConfig ctrl = loader.getController();
            ctrl.setNombreUsuario(username);

            Scene pantallaConfigScene = new Scene(pantallaConfigRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(pantallaConfigScene);
            stage.setTitle("Configuració de la Partida");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obrir la configuració: " + e.getMessage());
        }
    }
}