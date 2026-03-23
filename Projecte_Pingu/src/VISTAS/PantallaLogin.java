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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import GESTORES.GestorBBDD;

/**
 * Pantalla de login: l'usuari introdueix username i password
 * per carregar una partida guardada.
 */
public class PantallaLogin {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label feedbackLabel;
    @FXML private Button btnLogin;
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
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validacions
        if (username.isEmpty()) {
            mostrarError("⚠ Has d'introduir un nom d'usuari!");
            return;
        }
        if (password.isEmpty()) {
            mostrarError("⚠ Has d'introduir una contrasenya!");
            return;
        }

        if (conexion == null) {
            mostrarError("❌ No hi ha connexió a la base de dades!");
            return;
        }

        // Verificar credencials
        boolean loginOk = GestorBBDD.loginUsuario(conexion, username, password);
        if (!loginOk) {
            mostrarError("❌ Usuari o contrasenya incorrectes!");
            return;
        }

        // Buscar partida guardada
        if (!GestorBBDD.existePartidaGuardada(conexion, username)) {
            mostrarError("⚠ No tens cap partida guardada. Comença una partida nova!");
            return;
        }

        // Carregar dades de la partida
        LinkedHashMap<String, String> datos = GestorBBDD.cargarPartida(conexion, username);
        if (datos == null) {
            mostrarError("❌ Error carregant la partida guardada.");
            return;
        }

        feedbackLabel.setText("✅ Partida trobada! Carregant...");
        feedbackLabel.setStyle("-fx-text-fill: #22c55e;");

        // Obrir PantallaJuego amb les dades carregades
        cargarPartidaEnJuego(event, username, datos);
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        volverAlMenu(event);
    }

    private void mostrarError(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    private void cargarPartidaEnJuego(ActionEvent event, String username, LinkedHashMap<String, String> datos) {
        try {
            int numCasillas = Integer.parseInt(datos.get("NUM_CASILLAS"));
            int numJugadores = Integer.parseInt(datos.get("NUM_JUGADORES"));
            boolean focaActivada = "1".equals(datos.get("FOCA_ACTIVADA"));
            String nombresStr = datos.get("NOMBRES_JUGADORES");
            String posicionesStr = datos.get("POSICIONES_JUGADORES");

            // Parsejar noms
            ArrayList<String> noms = new ArrayList<>();
            if (nombresStr != null && !nombresStr.isEmpty()) {
                for (String nom : nombresStr.split(",")) {
                    noms.add(nom.trim());
                }
            }

            // Parsejar posicions
            ArrayList<Integer> posiciones = new ArrayList<>();
            if (posicionesStr != null && !posicionesStr.isEmpty()) {
                for (String pos : posicionesStr.split(",")) {
                    posiciones.add(Integer.parseInt(pos.trim()));
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
            Parent root = loader.load();

            PantallaJuego ctrl = loader.getController();
            ctrl.setNombreUsuario(username);
            ctrl.iniciarJoc(numCasillas, noms, focaActivada);

            // Restaurar posicions dels jugadors
            ctrl.restaurarPosiciones(posiciones);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Joc del Pingüí — Partida carregada de " + username);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("❌ Error carregant la partida: " + e.getMessage());
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
