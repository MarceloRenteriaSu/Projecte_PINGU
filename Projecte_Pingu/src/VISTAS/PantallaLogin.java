package VISTAS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.GaussianBlur;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import GESTORES.GestorBBDD;

public class PantallaLogin {

    // Fondo y logo
    @FXML private ImageView bgImageView;
    @FXML private ImageView logoImageView;

    // Overlay login
    @FXML private VBox loginFormOverlay;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label feedbackLabel;
    @FXML private Button btnLogin;

    // Overlay registro
    @FXML private VBox regFormOverlay;
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmField;
    @FXML private Label regFeedbackLabel;
    @FXML private Button btnRegistrar;

    // Botones dock
    @FXML private Button btnShowLogin;
    @FXML private Button btnShowReg;
    @FXML private StackPane mainContentPane;

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
        CursorManager.applyClickable(btnLogin);
        CursorManager.applyClickable(btnRegistrar);
        CursorManager.applyClickable(btnShowLogin);
        CursorManager.applyClickable(btnShowReg);

        // Logo
        try {
            java.net.URL url = getClass().getResource("title_pinguino.png");
            if (url != null) logoImageView.setImage(new Image(url.toExternalForm()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Dock / Background Blur ────────────────────────────────────

    private void blurBackground() {
        if (mainContentPane != null) mainContentPane.setEffect(new GaussianBlur(10));
    }

    private void clearBackground() {
        if (mainContentPane != null) mainContentPane.setEffect(null);
    }

    @FXML
    private void showLoginForm(ActionEvent event) {
        regFormOverlay.setVisible(false);
        regFeedbackLabel.setText("");
        loginFormOverlay.setVisible(true);
        blurBackground();
    }

    @FXML
    private void hideLoginForm(ActionEvent event) {
        loginFormOverlay.setVisible(false);
        feedbackLabel.setText("");
        clearBackground();
    }

    @FXML
    private void showRegForm(ActionEvent event) {
        loginFormOverlay.setVisible(false);
        feedbackLabel.setText("");
        regFormOverlay.setVisible(true);
        blurBackground();
    }

    @FXML
    private void hideRegForm(ActionEvent event) {
        regFormOverlay.setVisible(false);
        regFeedbackLabel.setText("");
        clearBackground();
    }

    // ── Login ─────────────────────────────────────────────────────

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().length() < 3 || username.contains(" ")) {
            feedbackLabel.setText("⚠ El usuario debe tener al menos 3 caracteres y sin espacios.");
        } else if (password == null || password.length() <= 3) {
            feedbackLabel.setText("⚠ La contraseña debe tener más de 3 caracteres.");
        } else {
            Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
            if (con == null) {
                feedbackLabel.setText("⚠ Error al conectar con la base de datos.");
            } else {
                boolean valid = GestorBBDD.loginUsuario(con, username.trim(), password);
                GestorBBDD.cerrar(con);
                if (valid) {
                    irAMenu(event, username.trim());
                } else {
                    feedbackLabel.setText("⚠ Usuario o contraseña incorrectos.");
                }
            }
        }
    }

    // ── Registro ──────────────────────────────────────────────────

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confirm  = regConfirmField.getText();

        if (username == null || username.trim().length() < 3 || username.contains(" ")) {
            regFeedbackLabel.setText("⚠ El usuario debe tener al menos 3 caracteres y sin espacios.");
        } else if (password == null || password.length() <= 3) {
            regFeedbackLabel.setText("⚠ La contraseña debe tener más de 3 caracteres.");
        } else if (!password.equals(confirm)) {
            regFeedbackLabel.setText("⚠ Las contraseñas no coinciden.");
        } else {
        Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        if (con == null) {
            regFeedbackLabel.setText("⚠ Error al conectar con la base de datos.");
        } else {
        int resultado = GestorBBDD.registrarUsuario(con, username.trim(), password);
        GestorBBDD.cerrar(con);

        if (resultado == 0) {
            regFormOverlay.setVisible(false);
            regFeedbackLabel.setText("");
            usernameField.setText(username.trim());
            loginFormOverlay.setVisible(true);
            feedbackLabel.setText("✓ Cuenta creada. ¡Ahora inicia sesión!");
        } else if (resultado == 1) {
            regFeedbackLabel.setText("⚠ El nombre de usuario ya existe. Elige otro.");
        } else {
            regFeedbackLabel.setText("⚠ Error al registrar. Inténtalo de nuevo.");
        }
        }
        }
    }

    // ── Navegación ────────────────────────────────────────────────

    private void irAMenu(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaMenu.fxml"));
            Parent root = loader.load();
            PantallaMenu ctrl = loader.getController();
            ctrl.setNombreUsuario(username);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene menuScene = new Scene(root);
            CursorManager.apply(menuScene);
            stage.setScene(menuScene);
            stage.setTitle("Menú Principal");
        } catch (Exception e) {
            e.printStackTrace();
            feedbackLabel.setText("⚠ Error al cargar el menú principal.");
        }
    }
}
