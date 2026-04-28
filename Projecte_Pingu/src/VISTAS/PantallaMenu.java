package VISTAS;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import GESTORES.GestorBBDD;

public class PantallaMenu {

    @FXML private Button btnNewMatch;
    @FXML private Button btnLoadMatch;
    @FXML private Button btnRanking;
    @FXML private Button btnAjustes;
    @FXML private Button btnCredits;
    @FXML private Button btnExit;
    @FXML private ImageView bgImageView;
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

        // Background stretches to fill window; apply cursor to scene
        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane parent = (StackPane) bgImageView.getParent();
                bgImageView.fitWidthProperty().bind(parent.widthProperty());
                bgImageView.fitHeightProperty().bind(parent.heightProperty());
                CursorManager.apply(newScene);
            }
        });

        try {
            conexion = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
        } catch (Exception e) {
            System.out.println("No se ha podido conectar a la BBDD: " + e.getMessage());
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
            mostrarAlerta("Error", "No hay conexión a la base de datos.");
            return;
        }

        try {
            // Open the match-selection screen as a modal dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaCargarPartida.fxml"));
            Parent root = loader.load();
            PantallaCargarPartida ctrl = loader.getController();
            ctrl.inicialitzar(conexion, nombreUsuarioLogueado);

            Stage selStage = new Stage();
            selStage.setTitle("Cargar Partida");
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
                stage.setTitle("El Juego del Pingüino — Partida Cargada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al abrir la pantalla de carga: " + e.getMessage());
        }
    }

    @FXML
    private void handleRanking(ActionEvent event) {
        if (conexion == null) {
            mostrarAlerta("Error", "No hay conexión a la base de datos.");
            return;
        }

        ArrayList<LinkedHashMap<String, String>> data = GestorBBDD.getRanking(conexion);
        ObservableList<LinkedHashMap<String, String>> items = FXCollections.observableArrayList(data);

        TableView<LinkedHashMap<String, String>> table = new TableView<>(items);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No hay datos de ranking."));

        TableColumn<LinkedHashMap<String, String>, String> colPos = new TableColumn<>("#");
        colPos.setCellValueFactory(cd ->
            new SimpleStringProperty(String.valueOf(items.indexOf(cd.getValue()) + 1)));
        colPos.setMaxWidth(40);
        colPos.setMinWidth(40);

        TableColumn<LinkedHashMap<String, String>, String> colUser = new TableColumn<>("Jugador");
        colUser.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("USERNAME", "")));

        TableColumn<LinkedHashMap<String, String>, String> colWins = new TableColumn<>("Ganadas");
        colWins.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("PARTIDAS_GANADAS", "0")));

        TableColumn<LinkedHashMap<String, String>, String> colPlayed = new TableColumn<>("Jugadas");
        colPlayed.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("PARTIDAS_JUGADAS", "0")));

        TableColumn<LinkedHashMap<String, String>, String> colRatio = new TableColumn<>("% Victorias");
        colRatio.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("RATIO", "0") + "%"));

        table.getColumns().addAll(colPos, colUser, colWins, colPlayed, colRatio);

        Label title = new Label("Ranking Mundial");
        title.getStyleClass().add("screen-title-label");

        Button btnClose = new Button("Cerrar");
        btnClose.getStyleClass().add("cp-dock-button");

        VBox root = new VBox(16, title, table, btnClose);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28));
        root.setStyle(
            "-fx-background-color: linear-gradient(" +
            "from 0% 0% to 100% 100%, #05101f 0%, #0b1e3c 30%, #0d2a52 55%, #081a3a 80%, #040d1c 100%);");

        Scene scene = new Scene(root, 580, 420);
        scene.getStylesheets().add(getClass().getResource("PantallaMenu.css").toExternalForm());

        Stage rankStage = new Stage();
        rankStage.initModality(Modality.APPLICATION_MODAL);
        rankStage.setTitle("Ranking");
        rankStage.setResizable(false);
        rankStage.setScene(scene);
        btnClose.setOnAction(e -> rankStage.close());
        rankStage.showAndWait();
    }

    @FXML
    private void handleAjustes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaAjustes.fxml"));
            Parent root = loader.load();
            Stage ajStage = new Stage();
            ajStage.initModality(Modality.APPLICATION_MODAL);
            ajStage.setTitle("Opciones");
            ajStage.setResizable(false);
            ajStage.setScene(new Scene(root));
            ajStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la pantalla de opciones.");
        }
    }

    @FXML
    private void handleCredits(ActionEvent event) {
        mostrarAlerta("Créditos", "Creadores del juego:\n- CARLOS OROS BENDEZÚ\n- MARCELO RENTERIA SU\n- DENIS TINEO DIAS");
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


    private void abrirPantallaConfig(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaConfig.fxml"));
            Parent pantallaConfigRoot = loader.load();

            PantallaConfig ctrl = loader.getController();
            ctrl.setNombreUsuario(username);
            ctrl.setConexion(conexion);

            Stage menuStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ctrl.setMenuStage(menuStage);

            Stage configStage = new Stage();
            configStage.initOwner(menuStage);
            configStage.initModality(Modality.WINDOW_MODAL);
            configStage.setTitle("Configuración de la Partida");
            // Match PantallaMenu window size
            configStage.setWidth(menuStage.getWidth());
            configStage.setHeight(menuStage.getHeight());
            configStage.setScene(new Scene(pantallaConfigRoot));
            configStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al abrir la configuración: " + e.getMessage());
        }
    }
}
