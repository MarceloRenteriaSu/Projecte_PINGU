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
    @FXML private Button btnCredits;
    @FXML private Button btnExit;
    @FXML private ImageView bgImageView;

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

        try {
            conexion = GestorBBDD.conectarBBDD("DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
            System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewMatch(ActionEvent event) {
        System.out.println("New Match clicked");
        abrirPantalla(event, "PantallaConfig.fxml", "Registre de Nou Usuari");
    }

    @FXML
    private void handleLoadMatch(ActionEvent event) {
        System.out.println("Load Match clicked");
        abrirPantalla(event, "PantallaLogin.fxml", "Carregar Partida");
    }

    @FXML
    private void handleCredits(ActionEvent event) {
        System.out.println("Credits clicked");
        // Mostra un diàleg simple amb els crèdits
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Crèdits");
        alert.setHeaderText("El Juego del Pingüí");
        alert.setContentText("Desenvolupat per DW2526_GR02\n\nGràcies per jugar! ❄🐧");
        alert.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        if (conexion != null) {
            GestorBBDD.cerrar(conexion);
        }
        System.exit(0);
    }

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
