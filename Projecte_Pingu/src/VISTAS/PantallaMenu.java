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

import java.sql.Connection;
import GESTORES.GestorBBDD;

public class PantallaMenu {

    @FXML private Button btnNewMatch;
    @FXML private Button btnLoadMatch;
    @FXML private Button btnExit;
    @FXML private ImageView bgImageView;

    // Connexió a la BBDD
    private Connection conexion = null;

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
            conexion = GestorBBDD.conectarBBDD("DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
            System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewMatch(ActionEvent event) {
        System.out.println("New Match clicked");
        abrirPantalla(event, "PantallaRegistro.fxml", "Registre de Nou Usuari");
    }

    @FXML
    private void handleLoadMatch(ActionEvent event) {
        System.out.println("Load Match clicked");
        abrirPantalla(event, "PantallaLogin.fxml", "Carregar Partida");
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

    /**
     * Obre una pantalla FXML genèrica.
     */
    private void abrirPantalla(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obrir " + fxmlFile + ": " + e.getMessage());
        }
    }
}