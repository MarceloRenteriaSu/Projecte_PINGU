package VISTAS;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.sql.Connection;
import GESTORES.GestorBBDD;

public class PantallaMenu {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private ComboBox<String> dificultadCombo;
    @FXML private Label feedbackLabel;

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    // Connexió a la BBDD (es manté oberta durant la sessió)
    private Connection conexion = null;

    @FXML
    private void initialize() {
        System.out.println("PantallaMenu initialized");

        // Omplir el ComboBox de dificultat
        dificultadCombo.setItems(FXCollections.observableArrayList("Normal", "Imposible"));
        dificultadCombo.setValue("Imposible"); // Per defecte: nivell imposible

        feedbackLabel.setText("");

        // Intentar connectar a la BBDD automaticament (fuera de centro)
        try {
            conexion = GestorBBDD.conectarBBDD("fuera", "", "");
        } catch (Exception e) {
            // Si no pot connectar, seguim sense BBDD
            System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewGame() {
        System.out.println("New Game clicked");
    }

    @FXML
    private void handleSaveGame() {
        System.out.println("Save Game clicked");
    }

    @FXML
    private void handleLoadGame() {
        System.out.println("Load Game clicked");
    }

    @FXML
    private void handleQuitGame() {
        System.out.println("Quit Game clicked");
        if (conexion != null) {
            GestorBBDD.cerrar(conexion);
        }
        System.exit(0);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = userField.getText().trim();
        String password = passField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("⚠ Introdueix usuari i contrasenya.");
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Intentar login amb BBDD
        boolean loginOk = false;
        if (conexion != null) {
            loginOk = GestorBBDD.loginUsuario(conexion, username, password);
            if (!loginOk) {
                feedbackLabel.setText("❌ Usuari o contrasenya incorrectes.");
                feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }
            feedbackLabel.setText("✅ Login correcte!");
            feedbackLabel.setStyle("-fx-text-fill: #22c55e;");
        } else {
            // Sense BBDD: acceptar qualsevol combinació no buida
            feedbackLabel.setText("✅ Mode offline — entrant sense validació.");
            feedbackLabel.setStyle("-fx-text-fill: #f59e0b;");
            loginOk = true;
        }

        if (loginOk) {
            abrirPantallaJuego(event, username);
        }
    }

    @FXML
    private void handleRegister() {
        String username = userField.getText().trim();
        String password = passField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("⚠ Introdueix usuari i contrasenya per registrar-te.");
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        if (conexion != null) {
            boolean registroOk = GestorBBDD.registrarUsuario(conexion, username, password);
            if (registroOk) {
                feedbackLabel.setText("✅ Registre correcte! Ara pots fer login.");
                feedbackLabel.setStyle("-fx-text-fill: #22c55e;");
            } else {
                feedbackLabel.setText("❌ Error al registrar. L'usuari potser ja existeix.");
                feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
            }
        } else {
            feedbackLabel.setText("⚠ No hi ha connexió a la BBDD. Mode offline actiu.");
            feedbackLabel.setStyle("-fx-text-fill: #f59e0b;");
        }
    }

    /**
     * Obre la pantalla de joc amb la dificultat seleccionada.
     */
    private void abrirPantallaJuego(ActionEvent event, String username) {
        try {
            String dificultat = dificultadCombo.getValue();
            boolean imposible = "Imposible".equalsIgnoreCase(dificultat);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
            Parent pantallaJuegoRoot = loader.load();

            // Passar paràmetres al controlador
            PantallaJuego ctrl = loader.getController();
            ctrl.setModoImposible(imposible);
            ctrl.setNombreUsuario(username);

            Scene pantallaJuegoScene = new Scene(pantallaJuegoRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(pantallaJuegoScene);
            stage.setTitle("Joc del Pingüí — " + dificultat);
        } catch (Exception e) {
            e.printStackTrace();
            feedbackLabel.setText("❌ Error al obrir el joc: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }
}