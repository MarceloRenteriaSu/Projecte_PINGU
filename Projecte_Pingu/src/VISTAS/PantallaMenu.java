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
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
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
        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane parent = (StackPane) bgImageView.getParent();
                bgImageView.fitWidthProperty().bind(parent.widthProperty());
                bgImageView.fitHeightProperty().bind(parent.heightProperty());
                CursorManager.apply(newScene);
                CursorManager.applyClickable(btnNewMatch);
                CursorManager.applyClickable(btnLoadMatch);
                CursorManager.applyClickable(btnRanking);
                CursorManager.applyClickable(btnAjustes);
                CursorManager.applyClickable(btnCredits);
                CursorManager.applyClickable(btnExit);
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
        abrirPantallaConfig(event, nombreUsuarioLogueado);
    }

    @FXML
    private void handleLoadMatch(ActionEvent event) {
        if (conexion == null) {
            mostrarAlerta("Error", "No hay conexión a la base de datos.");
        } else {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaCargarPartida.fxml"));
            Parent root = loader.load();
            PantallaCargarPartida ctrl = loader.getController();
            ctrl.inicialitzar(conexion, nombreUsuarioLogueado);

            Stage selStage = new Stage();
            selStage.setTitle("Cargar Partida");
            selStage.initModality(Modality.APPLICATION_MODAL);
            Scene selScene = new Scene(root, 720, 460);
            CursorManager.apply(selScene);
            selStage.setScene(selScene);
            selStage.setResizable(true);
            selStage.showAndWait();

            if (ctrl.isLoaded()) {
                LinkedHashMap<String, String> datos = ctrl.getSelectedPartida();
                FXMLLoader juegoLoader = new FXMLLoader(getClass().getResource("PantallaJuego.fxml"));
                Parent juegoRoot = juegoLoader.load();
                PantallaJuego juegoCtrl = juegoLoader.getController();
                juegoCtrl.setNombreUsuario(nombreUsuarioLogueado);
                juegoCtrl.restaurarPartida(datos);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene juegoScene = new Scene(juegoRoot);
                CursorManager.apply(juegoScene);
                stage.setScene(juegoScene);
                stage.setTitle("El Juego del Pingüino — Partida Cargada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al abrir la pantalla de carga: " + e.getMessage());
        }
        }
    }

    @FXML
    private void handleRanking(ActionEvent event) {
        mostrarRanking();
    }

    public void mostrarRanking() {
        if (conexion == null) {
            mostrarAlerta("Error", "No hay conexión a la base de datos.");
        } else {
        ArrayList<LinkedHashMap<String, String>> rankingData = GestorBBDD.getRankingPerJugades(conexion);
        int record = GestorBBDD.getRecord(conexion);
        ArrayList<String> jugadoresRecord = GestorBBDD.getJugadorsRecord(conexion);
        double media = GestorBBDD.getMitja(conexion);
        ArrayList<LinkedHashMap<String, String>> sobreMedia = GestorBBDD.getJugadorsSobreMitja(conexion);
        int myWins = GestorBBDD.getPartidasGanadasUsuario(conexion, nombreUsuarioLogueado);
        double myPct = GestorBBDD.getPctMenysGuanyades(conexion, myWins);

        // ---- TAB 1: Clasificación ----
        ObservableList<LinkedHashMap<String, String>> items = FXCollections.observableArrayList(rankingData);

        TableView<LinkedHashMap<String, String>> table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No hay datos de clasificación."));
        table.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 6; -fx-background-radius: 6;");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<LinkedHashMap<String, String>, String> colPos = new TableColumn<>("#");
        colPos.setCellValueFactory(cd ->
            new SimpleStringProperty(String.valueOf(items.indexOf(cd.getValue()) + 1)));
        colPos.setMaxWidth(40); colPos.setMinWidth(40);

        TableColumn<LinkedHashMap<String, String>, String> colUser = new TableColumn<>("Jugador");
        colUser.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("USERNAME", "")));

        TableColumn<LinkedHashMap<String, String>, String> colPlayed = new TableColumn<>("Partidas");
        colPlayed.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("PARTIDAS_JUGADES", cd.getValue().getOrDefault("PARTIDAS_JUGADAS", "0"))));
        colPlayed.setComparator((a, b) -> Integer.compare(parseIntSafe(a), parseIntSafe(b)));

        TableColumn<LinkedHashMap<String, String>, String> colWins = new TableColumn<>("Victorias");
        colWins.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("PARTIDAS_GANADAS", "0")));
        colWins.setComparator((a, b) -> Integer.compare(parseIntSafe(a), parseIntSafe(b)));

        TableColumn<LinkedHashMap<String, String>, String> colRatio = new TableColumn<>("% Victoria");
        colRatio.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOrDefault("RATIO", "0") + "%"));
        colRatio.setComparator((a, b) -> Double.compare(parseDoubleSafe(a), parseDoubleSafe(b)));

        table.getColumns().addAll(colPos, colUser, colPlayed, colWins, colRatio);

        VBox tabClasifContent = new VBox(10, table);
        tabClasifContent.setPadding(new Insets(12));
        VBox.setVgrow(table, Priority.ALWAYS);

        Tab tabClasif = new Tab("Clasificación", tabClasifContent);
        tabClasif.setClosable(false);

        // ---- TAB 2: Estadísticas ----
        String recordJugadores = jugadoresRecord.isEmpty()
            ? "Ningún jugador aún"
            : String.join(", ", jugadoresRecord);

        Label lblRecordTitle = makeSectionTitle("🏆  Récord mundial");
        Label lblRecord = makeStatLabel(recordJugadores + " — " + record + " victorias");

        Label lblMediaTitle = makeSectionTitle("📊  Media de victorias");
        Label lblMedia = makeStatLabel(String.format("%.2f victorias de media por jugador", media));

        String sobreMediaText;
        if (sobreMedia.isEmpty()) {
            sobreMediaText = "Ningún jugador supera la media";
        } else {
            StringBuilder sb = new StringBuilder();
            for (LinkedHashMap<String, String> row : sobreMedia) {
                if (sb.length() > 0) sb.append(",  ");
                sb.append(row.get("USERNAME")).append(" (").append(row.get("PARTIDAS_GANADAS")).append(" vic.)");
            }
            sobreMediaText = sb.toString();
        }
        Label lblSobreMediaTitle = makeSectionTitle("📈  Por encima de la media");
        Label lblSobreMedia = makeStatLabel(sobreMediaText);
        lblSobreMedia.setWrapText(true);

        Label lblPctTitle = makeSectionTitle("🐧  Tu posición — " + nombreUsuarioLogueado);
        Label lblPct = makeStatLabel(String.format(
            "%d victorias — superas al %.1f%% de los jugadores", myWins, myPct));

        VBox tabStatsContent = new VBox(8,
            lblRecordTitle, lblRecord, new Separator(),
            lblMediaTitle, lblMedia, new Separator(),
            lblSobreMediaTitle, lblSobreMedia, new Separator(),
            lblPctTitle, lblPct
        );
        tabStatsContent.setPadding(new Insets(16));

        Tab tabStats = new Tab("Estadísticas", tabStatsContent);
        tabStats.setClosable(false);

        // ---- Composición final ----
        TabPane tabPane = new TabPane(tabClasif, tabStats);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Label title = new Label("Ranking Mundial");
        title.getStyleClass().add("screen-title-label");

        Button btnClose = new Button("Cerrar");
        btnClose.getStyleClass().add("cp-dock-button");
        btnClose.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(14, title, tabPane, btnClose);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle(
            "-fx-background-color: linear-gradient(" +
            "from 0% 0% to 100% 100%, #05101f 0%, #0b1e3c 30%, #0d2a52 55%, #081a3a 80%, #040d1c 100%);");

        Scene scene = new Scene(root, 620, 480);
        scene.getStylesheets().add(getClass().getResource("PantallaMenu.css").toExternalForm());
        CursorManager.apply(scene);

        Stage rankStage = new Stage();
        rankStage.initModality(Modality.APPLICATION_MODAL);
        rankStage.setTitle("Ranking Mundial");
        rankStage.setResizable(true);
        rankStage.setScene(scene);
        btnClose.setOnAction(e -> rankStage.close());
        rankStage.showAndWait();
        }
    }

    private Label makeSectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #7ec8f7; -fx-font-family: 'Arial';");
        return lbl;
    }

    private Label makeStatLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #c8e6f9; -fx-font-family: 'Arial'; -fx-padding: 0 0 0 12;");
        return lbl;
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
            Scene ajScene = new Scene(root);
            CursorManager.apply(ajScene);
            ajStage.setScene(ajScene);
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
            configStage.setWidth(menuStage.getWidth());
            configStage.setHeight(menuStage.getHeight());
            Scene configScene = new Scene(pantallaConfigRoot);
            CursorManager.apply(configScene);
            configStage.setScene(configScene);
            configStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al abrir la configuración: " + e.getMessage());
        }
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.replace("%", "").trim()); } catch (Exception e) { return 0.0; }
    }
}
