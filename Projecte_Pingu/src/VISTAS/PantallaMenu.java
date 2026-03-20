package VISTAS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML private Label feedbackLabel;

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    // Connexió a la BBDD
    private Connection conexion = null;

    @FXML
    private void initialize() {
        System.out.println("PantallaMenu initialized");
        feedbackLabel.setText("");

        // Connectar a la BBDD amb les credencials hardcodeades
        try {
            conexion = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
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
            feedbackLabel.setText("✅ Mode offline — entrant sense validació.");
            feedbackLabel.setStyle("-fx-text-fill: #f59e0b;");
            loginOk = true;
        }

        if (loginOk) {
            abrirPantallaConfig(event, username);
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
            feedbackLabel.setText("❌ Error al obrir la configuració: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }
}