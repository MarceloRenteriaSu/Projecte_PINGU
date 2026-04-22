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

public class PantallaLogin {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label feedbackLabel;
    @FXML private Button btnLogin;
    @FXML private Button btnRegistrar;

    @FXML
    private void initialize() {
        CursorManager.applyWhenReady(usernameField);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().length() < 3 || username.contains(" ")) {
            mostrarError("L'usuari ha de tenir almenys 3 caràcters i no contenir espais.");
            return;
        }

        if (password == null || password.length() <= 3) {
            mostrarError("La contrasenya ha de tenir més de 3 caràcters.");
            return;
        }

        // Connexió a la BBDD
        Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        if (con == null) {
            mostrarError("Error en connectar a la base de dades.");
            return;
        }

        boolean valid = GestorBBDD.loginUsuario(con, username.trim(), password);
        GestorBBDD.cerrar(con);

        if (valid) {
            irAMenu(event, username.trim());
        } else {
            mostrarError("Credencials incorrectes. Torna-ho a provar.");
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaRegistro.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("El Joc del Pingu — Registre");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al carregar la pantalla de registre.");
        }
    }

    private void mostrarError(String mensaje) {
        feedbackLabel.setText("⚠ " + mensaje);
        feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    private void irAMenu(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaMenu.fxml"));
            Parent root = loader.load();
            
            PantallaMenu ctrl = loader.getController();
            ctrl.setNombreUsuario(username);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menú Principal");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al carregar el menú principal.");
        }
    }
}
