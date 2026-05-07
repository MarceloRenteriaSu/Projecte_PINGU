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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import GESTORES.GestorBBDD;

public class PantallaRegistro {

    @FXML private ImageView bgImageView;
    @FXML private ImageView logoImageView;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label feedbackLabel;
    @FXML private Button btnRegistrar;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        // Fondo
        try {
            java.io.InputStream isBg = getClass().getResourceAsStream("menu_background.png");
            if (isBg != null) bgImageView.setImage(new Image(isBg));
        } catch (Exception e) { e.printStackTrace(); }

        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane parent = (StackPane) bgImageView.getParent();
                bgImageView.fitWidthProperty().bind(parent.widthProperty());
                bgImageView.fitHeightProperty().bind(parent.heightProperty());
                CursorManager.apply(newScene);
            }
        });
        CursorManager.applyClickable(btnRegistrar);
        CursorManager.applyClickable(btnVolver);

        // Logo
        try {
            java.net.URL url = getClass().getResource("title_pinguino.png");
            if (url != null) logoImageView.setImage(new Image(url.toExternalForm()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        if (username == null || username.trim().length() < 3 || username.contains(" ")) {
            mostrarError("El usuario debe tener al menos 3 caracteres y sin espacios.");
            return;
        }
        if (password == null || password.length() <= 3) {
            mostrarError("La contraseña debe tener más de 3 caracteres.");
            return;
        }
        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        if (con == null) {
            mostrarError("Error al conectar con la base de datos.");
            return;
        }

        int resultado = GestorBBDD.registrarUsuario(con, username.trim(), password);
        GestorBBDD.cerrar(con);

        if (resultado == 0) {
            irALogin(event);
        } else if (resultado == 1) {
            mostrarError("El nombre de usuario ya existe. Elige otro.");
        } else {
            mostrarError("Error al registrar. Inténtalo de nuevo.");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        irALogin(event);
    }

    private void mostrarError(String mensaje) {
        feedbackLabel.setText("⚠ " + mensaje);
    }

    private void irALogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene loginScene = new Scene(root);
            CursorManager.apply(loginScene);
            stage.setScene(loginScene);
            stage.setTitle("El Juego del Pingüino — Login");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar la pantalla de login.");
        }
    }
}
