package VISTAS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import GESTORES.GestorBBDD;

/**
 * Pantalla de registre: l'usuari crea un compte nou (username + password)
 * i després passa a PantallaConfig per configurar la partida.
 */
public class PantallaRegistro {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label feedbackLabel;
    @FXML private Button btnRegistrar;
    @FXML private Button btnVolver;

    private Connection conexion = null;

    @FXML
    private void initialize() {
        feedbackLabel.setText("");
        // Connectar a la BBDD
        try {
            conexion = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
            System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        // Validacions
        if (username.isEmpty()) {
            mostrarError("⚠ Has d'introduir un nom d'usuari!");
            return;
        }
        if (username.length() > 50) {
            mostrarError("⚠ El nom d'usuari no pot tenir més de 50 caràcters!");
            return;
        }
        if (password.isEmpty()) {
            mostrarError("⚠ Has d'introduir una contrasenya!");
            return;
        }
        if (!password.equals(confirm)) {
            mostrarError("⚠ Les contrasenyes no coincideixen!");
            return;
        }

        if (conexion == null) {
            mostrarError("❌ No hi ha connexió a la base de dades!");
            return;
        }

        // Intentar registrar
        boolean registrat = GestorBBDD.registrarUsuario(conexion, username, password);
        if (registrat) {
            feedbackLabel.setText("✅ Registre correcte! Obrint configuració...");
            feedbackLabel.setStyle("-fx-text-fill: #22c55e;");
            // Obrir PantallaConfig amb el nom d'usuari
            abrirPantallaConfig(event, username);
        } else {
            mostrarError("❌ No s'ha pogut registrar. L'usuari ja existeix?");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        volverAlMenu(event);
    }

    private void mostrarError(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    private void abrirPantallaConfig(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaConfig.fxml"));
            Parent root = loader.load();

            PantallaConfig ctrl = loader.getController();
            ctrl.setNombreUsuario(username);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Configuració de la Partida");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("❌ Error obrint la configuració: " + e.getMessage());
        }
    }

    private void volverAlMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("El Juego del Pingüino");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
