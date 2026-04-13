package VISTAS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.util.LinkedHashMap;

import GESTORES.GestorBBDD;

public class PantallaMenu {

    @FXML private Button btnNewMatch;
    @FXML private Button btnLoadMatch;
    @FXML private Button btnAjustes;
    @FXML private Button btnCredits;
    @FXML private Button btnExit;
    @FXML private ImageView bgImageView;
    @FXML private ImageView titleImageView;
    @FXML private Label usernameLabel;

    private Connection conexion = null;
    private String nombreUsuarioLogueado = "guest";

    public void setNombreUsuario(String username) {
        this.nombreUsuarioLogueado = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Jugador: " + username);
        }
    }

    @FXML
    private void initialize() {
        System.out.println("PantallaMenu initialized");

        // Background image stretches to fill the window
        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane parent = (StackPane) bgImageView.getParent();
                bgImageView.fitWidthProperty().bind(parent.widthProperty());
                bgImageView.fitHeightProperty().bind(parent.heightProperty());
            }
        });

        // Title image: remove black background pixels, then scale with window
        if (titleImageView != null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream("title_pinguino.png");
                if (is != null) {
                    Image raw = new Image(is);
                    titleImageView.setImage(removeBlackBackground(raw));
                }
            } catch (Exception e) {
                System.out.println("Error loading title: " + e.getMessage());
            }
            titleImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    StackPane root = (StackPane) titleImageView.getScene().getRoot();
                    titleImageView.fitWidthProperty().bind(root.widthProperty().multiply(0.48));
                }
            });
        }

        try {
            conexion = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
            System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewMatch(ActionEvent event) {
        System.out.println("Nueva Partida clicked");
        abrirPantallaConfig(event, nombreUsuarioLogueado);
    }

    @FXML
    private void handleLoadMatch(ActionEvent event) {
        System.out.println("Cargar Partida clicked");
        if (conexion == null) {
            mostrarAlerta("Error", "No hi ha connexió a la base de dades.");
            return;
        }

        try {
            // Open the match-selection screen as a modal dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaCargarPartida.fxml"));
            Parent root = loader.load();
            PantallaCargarPartida ctrl = loader.getController();
            ctrl.inicialitzar(conexion, nombreUsuarioLogueado);

            Stage selStage = new Stage();
            selStage.setTitle("Carregar Partida");
            selStage.initModality(Modality.APPLICATION_MODAL);
            selStage.setScene(new Scene(root, 720, 460));
            selStage.setResizable(true);
            selStage.showAndWait();

            // After the dialog closes, check if the user selected a match
            if (ctrl.isLoaded()) {
                LinkedHashMap<String, String> datos = ctrl.getSelectedPartida();
                FXMLLoader juegoLoader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
                Parent juegoRoot = juegoLoader.load();
                PantallaJuego juegoCtrl = juegoLoader.getController();
                juegoCtrl.setNombreUsuario(nombreUsuarioLogueado);
                juegoCtrl.restaurarPartida(datos);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(juegoRoot));
                stage.setTitle("El Joc del Pingu — Partida Carregada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al obrir la pantalla de càrrega: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjustes(ActionEvent event) {
        System.out.println("Ajustes clicked");
        mostrarAlerta("Ajustes", "Pantalla de ajustes en desarrollo.");
    }

    @FXML
    private void handleCredits(ActionEvent event) {
        mostrarAlerta("Creditos", "Creadors del joc:\n- CARLOS OROS BENDEZÚ\n- MARCELO RENTERIA SU\n- DENIS TINEO DIAS");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        if (conexion != null) {
            GestorBBDD.cerrar(conexion);
        }
        System.exit(0);
    }

    private Image removeBlackBackground(Image original) {
        int w = (int) original.getWidth();
        int h = (int) original.getHeight();
        WritableImage result = new WritableImage(w, h);
        PixelReader reader = original.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                double brightness = c.getBrightness();
                if (brightness < 0.12) {
                    writer.setColor(x, y, Color.TRANSPARENT);
                } else if (brightness < 0.30) {
                    double alpha = (brightness - 0.12) / 0.18;
                    writer.setColor(x, y, new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                } else {
                    writer.setColor(x, y, c);
                }
            }
        }
        return result;
    }

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
}
