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

public class PantallaRegistro {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label feedbackLabel;
    @FXML private Button btnRegistrar;
    @FXML private Button btnVolver;

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username == null || username.trim().length() < 3 || username.contains(" ")) {
            mostrarError("L'usuari ha de tenir almenys 3 caràcters i no contenir espais.");
            return;
        }

        if (password == null || password.length() <= 3) {
            mostrarError("La contrasenya ha de tenir més de 3 caràcters.");
            return;
        }

        if (!password.equals(confirm)) {
            mostrarError("Les contrasenyes no coincideixen.");
            return;
        }

        // Connexió a la BBDD
        Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        if (con == null) {
            mostrarError("Error en connectar a la base de dades.");
            return;
        }

        boolean ok = GestorBBDD.registrarUsuario(con, username.trim(), password);
        GestorBBDD.cerrar(con);

        if (ok) {
            System.out.println("Registre completat amb èxit.");
            irALogin(event);
        } else {
            mostrarError("Error al registrar. Potser l'usuari ja existeix.");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        irALogin(event);
    }
    
    private void mostrarError(String mensaje) {
        feedbackLabel.setText("⚠ " + mensaje);
        feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    private void irALogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("El Joc del Pingu — Login");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al carregar la pantalla de login.");
        }
    }
}
