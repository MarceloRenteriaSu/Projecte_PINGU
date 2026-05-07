package VISTAS;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import GESTORES.GestorPartida;
import GESTORES.GestorBBDD;
import MODELOS.*;

public class PantallaJuego {

	// Auto-play
	@FXML private Button btnAutoPlay;
	private Timeline autoPlayTimeline;
	private boolean autoPlayOn = false;
	private boolean isMoving   = false;

	// Menu items
	@FXML private MenuItem newGame;
	@FXML private MenuItem saveGame;
	@FXML private MenuItem loadGame;
	@FXML private MenuItem quitGame;

	// Buttons
	@FXML private Button dado;

	// Inventory slots
	@FXML private StackPane slotRapido;
	@FXML private StackPane slotLento;
	@FXML private StackPane slotPeces;
	@FXML private StackPane slotBola;
	@FXML private Label qtyRapido;
	@FXML private Label qtyLento;
	@FXML private Label qtyPeces;
	@FXML private Label qtyBola;
	@FXML private VBox usarBox;
	@FXML private Button usarBtn;
	@FXML private ImageView iconRapido;
	private String selectedItem = null;

	// Texts
	@FXML private Text dadoResultText;

	// Events list
	@FXML private ListView<String> eventosListView;
	private final ObservableList<String> eventosData = FXCollections.observableArrayList();

	// Game board and player pieces (up to 4 pingüinos + 1 foca)
	@FXML private GridPane tablero;
	@FXML private Canvas pathCanvas;
	@FXML private Canvas P1;
	@FXML private Canvas P2;
	@FXML private Canvas P3;
	@FXML private Canvas P4;
	@FXML private Canvas P5;

	private GestorPartida gestorPartida;
	private int columnas;
	private int filas;

	private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";
	private final Random rand = new Random();

	// posiciones[0..N-1] = pingüinos, posiciones[N] = foca (si està activada)
	private int[] posiciones;
	private Node[] fichas;

	// Índex de la foca dins la llista de jugadors de la partida (-1 si desactivada)
	private int indiceFoca = -1;

	// Si la foca està activada o no
	private boolean focaActivada = true;

	// Comptador de torns de pingüins completats en la ronda actual
	private int turnosPinguinosEnRonda = 0;
	private int totalPinguinos = 4;

	// Nom del jugador logejat
	private String nombreUsuarioLogueado = "Jugador";

	// ID de la partida carregada des de la BBDD (-1 si és partida nova sense guardar)
	private int partidaGuardadaId = -1;

	private boolean jocIniciat = false;


	public void iniciarJoc(int numCasillas, ArrayList<String> noms, ArrayList<String> hexColors, boolean ambFoca) {
		if (!jocIniciat) {
			configurarJoc(numCasillas, noms, hexColors, ambFoca);
			jocIniciat = true;
		}
	}

	public void setNombreUsuario(String nom) {
		this.nombreUsuarioLogueado = nom;
	}

	@FXML
	private void initialize() {
		CursorManager.applyWhenReady(dado);
		CursorManager.applyClickable(dado);
		CursorManager.applyClickable(btnAutoPlay);
		CursorManager.applyClickable(usarBtn);
		setupItemSlots();
		pathCanvas.widthProperty().bind(tablero.widthProperty());
		pathCanvas.heightProperty().bind(tablero.heightProperty());
		pathCanvas.widthProperty().addListener((obs, ov, nv) -> drawPath());
		pathCanvas.heightProperty().addListener((obs, ov, nv) -> drawPath());

		eventosListView.setItems(eventosData);
		eventosListView.setCellFactory(lv -> new ListCell<String>() {
			private final Text text = new Text();
			{
				text.wrappingWidthProperty().bind(lv.widthProperty().subtract(28));
				text.getStyleClass().add("stat");
				setPrefWidth(0);
			}
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) { setGraphic(null); }
				else { text.setText(item); setGraphic(text); }
			}
		});
	}

	private void setupItemSlots() {
		// Load dado_rapido icon
		try {
			java.net.URL iconUrl = getClass().getResource("/pngs_iconos/dado_rapido.png");
			if (iconUrl != null) iconRapido.setImage(new Image(iconUrl.toExternalForm()));
		} catch (Exception e) { /* icon missing, slot still works */ }

		// Colored borders per slot
		slotRapido.setStyle("-fx-border-color:#e74c3c;-fx-border-width:3;-fx-border-radius:8;-fx-background-color:rgba(255,255,255,0.22);-fx-background-radius:8;");
		slotLento .setStyle("-fx-border-color:#2980b9;-fx-border-width:3;-fx-border-radius:8;-fx-background-color:rgba(255,255,255,0.22);-fx-background-radius:8;");
		slotPeces .setStyle("-fx-border-color:#27ae60;-fx-border-width:3;-fx-border-radius:8;-fx-background-color:rgba(255,255,255,0.22);-fx-background-radius:8;");
		slotBola  .setStyle("-fx-border-color:#e91e8c;-fx-border-width:3;-fx-border-radius:8;-fx-background-color:rgba(255,255,255,0.22);-fx-background-radius:8;");

		// Passive slots: always non-clickable
		slotPeces.setMouseTransparent(true);
		slotBola .setMouseTransparent(true);

		// Bind managed to visible so Usar area takes no space when hidden
		usarBox.managedProperty().bind(usarBox.visibleProperty());
	}

	// -------------------------------------------------------
	// ITEM SLOT SELECTION HELPERS
	// -------------------------------------------------------

	private void setSlotEnabled(StackPane slot, boolean enabled) {
		slot.setOpacity(enabled ? 1.0 : 0.35);
		slot.setMouseTransparent(!enabled);
	}

	private void setSlotSelected(StackPane slot, boolean selected) {
		slot.setEffect(selected ? new DropShadow(14, javafx.scene.paint.Color.WHITE) : null);
	}

	private void clearSelection() {
		selectedItem = null;
		setSlotSelected(slotRapido, false);
		setSlotSelected(slotLento,  false);
		usarBox.setVisible(false);
	}

	private void addEvent(String text) {
		eventosData.add(text);
		eventosListView.scrollTo(eventosData.size() - 1);
	}

	private void drawPath() {
		if (gestorPartida == null || gestorPartida.getPartida() == null) return;
		double w = pathCanvas.getWidth();
		double h = pathCanvas.getHeight();
		if (w <= 0 || h <= 0) return;

		javafx.geometry.Insets ins = tablero.getInsets();
		double padL = ins.getLeft();
		double padT = ins.getTop();
		double boardW = w - padL - ins.getRight();
		double boardH = h - padT - ins.getBottom();
		if (boardW <= 0 || boardH <= 0) return;

		double cellW = boardW / this.columnas;
		double cellH = boardH / this.filas;
		double margin  = Math.min(cellW, cellH) * 0.07;
		double arc     = Math.min(cellW, cellH) * 0.22;
		double connH   = cellH * 0.36;
		double connV   = cellW * 0.36;

		int total = gestorPartida.getPartida().getTablero().getTamanyo();

		GraphicsContext gc = pathCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, w, h);
		gc.setFill(Color.WHITE);

		// Connectors first (drawn behind tiles)
		for (int i = 0; i < total - 1; i++) {
			int[] a = obtenerFilaColumna(i);
			int[] b = obtenerFilaColumna(i + 1);
			double ax = padL + a[1] * cellW + cellW / 2;
			double ay = padT + a[0] * cellH + cellH / 2;
			double bx = padL + b[1] * cellW + cellW / 2;
			double by = padT + b[0] * cellH + cellH / 2;
			if (a[0] == b[0]) {
				gc.fillRect(Math.min(ax, bx), ay - connH / 2, Math.abs(bx - ax), connH);
			} else {
				gc.fillRect(ax - connV / 2, Math.min(ay, by), connV, Math.abs(by - ay));
			}
		}

		// Tiles on top of connectors
		for (int i = 0; i < total; i++) {
			int[] rc = obtenerFilaColumna(i);
			double x  = padL + rc[1] * cellW + margin;
			double y  = padT + rc[0] * cellH + margin;
			gc.fillRoundRect(x, y, cellW - 2 * margin, cellH - 2 * margin, arc, arc);
		}
	}

	/**
	 * Configura el joc complet: tablero, jugadors, fitxes centrades.
	 */
	private void configurarJoc(int totalCasillas, ArrayList<String> noms, ArrayList<String> hexColors, boolean ambFoca) {
		eventosData.clear();
		addEvent("¡El juego ha comenzado!");
		this.focaActivada = ambFoca;

		// Incrementar PARTIDAS_JUGADAS per a tots els jugadors de la partida
		java.sql.Connection conStats = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
		if (conStats != null) {
			for (String nom : noms) {
				GestorBBDD.incrementarPartidasJugadas(conStats, nom);
			}
			GestorBBDD.cerrar(conStats);
		}

		Tablero t = new Tablero(totalCasillas);

		this.columnas = (int) Math.ceil(Math.sqrt(t.getTamanyo()));
		this.filas    = (int) Math.ceil((double) t.getTamanyo() / this.columnas);

		tablero.getColumnConstraints().clear();
		tablero.getRowConstraints().clear();

		for (int i = 0; i < this.columnas; i++) {
			javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
			cc.setPercentWidth(100.0 / this.columnas);
			tablero.getColumnConstraints().add(cc);
		}
		for (int i = 0; i < this.filas; i++) {
			javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
			rc.setPercentHeight(100.0 / this.filas);
			tablero.getRowConstraints().add(rc);
		}

		gestorPartida = new GestorPartida();

		ArrayList<Jugador> jugadors = new ArrayList<>();

		// Crear pingüinos segons els noms rebuts
		int numPinguinos = noms.size();
		totalPinguinos = numPinguinos;
		String[] colors = {"Azul", "Rojo", "Verde", "Amarillo"};
		for (int i = 0; i < numPinguinos; i++) {
			ArrayList<Item> items = new ArrayList<>();
			items.add(new Dado("Normal", 1));
			Pinguino p = new Pinguino(noms.get(i), 0, colors[i % colors.length], new Inventario(items));
			p.setJuega(true); // Nou pingüí pot jugar el seu primer torn
			jugadors.add(p);
		}

		// Foca (CPU) — només si està activada
		if (focaActivada) {
			Foca foca = new Foca(0);
			jugadors.add(foca);
			indiceFoca = jugadors.size() - 1;
		} else {
			indiceFoca = -1;
		}

		gestorPartida.nuevaPartida(t, jugadors);

		// Configurar les fitxes visuals segons el nombre de jugadors
		Canvas[] totesLesFitxes = {P1, P2, P3, P4};
		for (Canvas c : totesLesFitxes) c.setVisible(false);

		if (focaActivada) {
			fichas = new Node[numPinguinos + 1];
			posiciones = new int[numPinguinos + 1];
			for (int i = 0; i < numPinguinos; i++) {
				fichas[i] = totesLesFitxes[i];
				totesLesFitxes[i].setVisible(true);
				posiciones[i] = 0;
				Color hatColor = (hexColors != null && i < hexColors.size())
					? Color.web(hexColors.get(i))
					: PinguinoRenderer.DEFAULT_COLORS[i % PinguinoRenderer.DEFAULT_COLORS.length];
				PinguinoRenderer.draw(totesLesFitxes[i].getGraphicsContext2D(),
					PinguinoRenderer.GAME_PX, hatColor, false);
			}
			fichas[numPinguinos] = P5;
			P5.setVisible(true);
			posiciones[numPinguinos] = 0;
			PinguinoRenderer.draw(P5.getGraphicsContext2D(), PinguinoRenderer.GAME_PX, null, true);
		} else {
			fichas = new Node[numPinguinos];
			posiciones = new int[numPinguinos];
			for (int i = 0; i < numPinguinos; i++) {
				fichas[i] = totesLesFitxes[i];
				totesLesFitxes[i].setVisible(true);
				posiciones[i] = 0;
				Color hatColor = (hexColors != null && i < hexColors.size())
					? Color.web(hexColors.get(i))
					: PinguinoRenderer.DEFAULT_COLORS[i % PinguinoRenderer.DEFAULT_COLORS.length];
				PinguinoRenderer.draw(totesLesFitxes[i].getGraphicsContext2D(),
					PinguinoRenderer.GAME_PX, hatColor, false);
			}
			P5.setVisible(false);
		}

		// Centrar totes les fitxes amb setHalignment/setValignment
		for (int i = 0; i < fichas.length; i++) {
			GridPane.setRowIndex(fichas[i], 0);
			GridPane.setColumnIndex(fichas[i], 0);
			GridPane.setHalignment(fichas[i], javafx.geometry.HPos.CENTER);
			GridPane.setValignment(fichas[i], javafx.geometry.VPos.CENTER);
			GridPane.setMargin(fichas[i], javafx.geometry.Insets.EMPTY);
		}
		// Offsets petits perquè no es sobreposin dins la mateixa casella
		distribuirFichasEnCasilla(0);

		mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
		actualizarInventarioUI();
		marcarJugadorActual();

		turnosPinguinosEnRonda = 0;

		// Si la foca no está activada, deshabilitar slot de peces
		if (!focaActivada) {
			setSlotEnabled(slotPeces, false);
		}
	}

	// -------------------------------------------------------
	// DISTRIBUIR FICHAS DINS UNA CASELLA (evitar superposició)
	// -------------------------------------------------------

	private double[][] getOffsets() {
		// TOKEN_W = 64px → offset ±33 gives spacing 66px > 64px (no overlap)
		int n = fichas.length;
		if (n == 2) {
			return new double[][] {
				{-33,  0},
				{ 33,  0}
			};
		} else if (n == 3) {
			return new double[][] {
				{-33, -24},
				{ 33, -24},
				{  0,  24}
			};
		} else if (n == 4) {
			if (focaActivada) { // 3P + foca
				return new double[][] {
					{-33, -24},
					{ 33, -24},
					{-33,  24},
					{  0,   0}
				};
			} else { // 4P sin foca
				return new double[][] {
					{-33, -24},
					{ 33, -24},
					{-33,  24},
					{ 33,  24}
				};
			}
		} else { // 5 (4P + foca)
			return new double[][] {
				{-33, -24},
				{ 33, -24},
				{-33,  24},
				{ 33,  24},
				{  0,   0}
			};
		}
	}

	private void distribuirFichasEnCasilla(int posTablero) {
		double[][] offsets = getOffsets();
		for (int i = 0; i < fichas.length; i++) {
			if (posiciones[i] == posTablero) {
				fichas[i].setTranslateX(offsets[i][0]);
				fichas[i].setTranslateY(offsets[i][1]);
			}
		}
	}

	private void redistribuirFichasEnPosicion(int pos) {
		double[][] offsets = getOffsets();
		for (int i = 0; i < fichas.length; i++) {
			if (posiciones[i] == pos) {
				fichas[i].setTranslateX(offsets[i][0]);
				fichas[i].setTranslateY(offsets[i][1]);
			}
		}
	}

	// -------------------------------------------------------
	// HELPERS DE POSICIONAMENT
	// -------------------------------------------------------

	private int[] obtenerFilaColumna(int posicion) {
		int row = posicion / this.columnas;
		int col;
		if (row % 2 == 0) {
			col = posicion % this.columnas;
		} else {
			col = this.columnas - 1 - (posicion % this.columnas);
		}
		return new int[]{row, col};
	}

	private void mostrarTiposDeCasillasEnTablero(Tablero t) {
		tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));
		int totalCasillas = t.getTamanyo();
		for (int i = 0; i < totalCasillas; i++) {
			Casilla casilla = t.getCasillas().get(i);
			String tipo = casilla.getClass().getSimpleName();

			String imgName;
			switch (tipo) {
				case "Oso":             imgName = "casilla_oso.png";         break;
				case "Agujero":         imgName = "casilla_agujero.png";     break;
				case "Trineo":          imgName = "casilla_trineo.png";      break;
				case "SueloQuebradizo": imgName = "casilla_quebradizo.png";  break;
				case "Normal":          imgName = "casilla_normal.png";      break;
				default:                imgName = "casilla_interrogante.png"; break;
			}

			ImageView iv = new ImageView();
			try {
				// Load at a capped resolution so it never overwhelms the grid cell
				iv.setImage(new Image(
					getClass().getResourceAsStream("/pngs_casillas/" + imgName),
					56, 56, true, true));
			} catch (Exception e) {
				System.out.println("Could not load " + imgName);
			}
			iv.setPreserveRatio(true);

			String numLabel = (i == 0) ? "S" : (i == totalCasillas - 1) ? "F" : String.valueOf(i);
			Text txt = new Text(numLabel);
			txt.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
			txt.getStyleClass().add(i == 0 || i == totalCasillas - 1 ? "cell-title" : "cell-type");

			StackPane cell = new StackPane();
			cell.getChildren().addAll(iv, txt);
			StackPane.setAlignment(iv, javafx.geometry.Pos.CENTER);
			StackPane.setAlignment(txt, javafx.geometry.Pos.TOP_LEFT);
			StackPane.setMargin(txt, new javafx.geometry.Insets(2, 0, 0, 3));
			cell.setMaxWidth(Double.MAX_VALUE);
			cell.setMaxHeight(Double.MAX_VALUE);
			// Scale to 78 % of cell so there is always padding around the image
			iv.fitWidthProperty().bind(cell.widthProperty().multiply(0.78));
			iv.fitHeightProperty().bind(cell.heightProperty().multiply(0.78));

			cell.setUserData(TAG_CASILLA_TEXT);
			int[] pos = obtenerFilaColumna(i);
			GridPane.setRowIndex(cell, pos[0]);
			GridPane.setColumnIndex(cell, pos[1]);
			tablero.getChildren().add(cell);
		}

		// Bring player tokens above the cell images
		for (Canvas c : new Canvas[]{P1, P2, P3, P4, P5}) {
			if (c != null && tablero.getChildren().contains(c)) c.toFront();
		}

		javafx.application.Platform.runLater(this::drawPath);
	}

	// -------------------------------------------------------
	// MARCAR JUGADOR ACTUAL (indicador visual)
	// -------------------------------------------------------

	private void marcarJugadorActual() {
		for (int i = 0; i < fichas.length; i++) {
			boolean esFocaToken = focaActivada && i == indiceFoca;
			fichas[i].setEffect(new DropShadow(8, 0, 0,
				esFocaToken ? Color.web("#dc2626") : Color.rgb(255, 255, 255, 0.5)));
		}
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		if (indice < fichas.length && indice != indiceFoca) {
			fichas[indice].setEffect(new DropShadow(14, 0, 0, Color.GOLD));
		}
	}

	// -------------------------------------------------------
	// ACTUALITZAR UI INVENTARI
	// -------------------------------------------------------

	private void actualizarInventarioUI() {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();

		if (focaActivada && indice == indiceFoca) {
			dado.setDisable(true);
			setSlotEnabled(slotRapido, false);
			setSlotEnabled(slotLento,  false);
			setSlotEnabled(slotPeces,  false);
			setSlotEnabled(slotBola,   false);
			dadoResultText.setText("Turno de la Foca (CPU)");
			return;
		}

		Jugador j = partida.getJugadores().get(indice);
		if (!(j instanceof Pinguino)) return;
		Pinguino pingu = (Pinguino) j;
		Inventario inv = pingu.getInv();

		dado.setDisable(autoPlayOn);

		int nRapido = inv.contarItem(new Dado("Rapido", 0));
		int nLento  = inv.contarItem(new Dado("Lento", 0));
		int nPeces  = inv.contarItem(new Pez(0));
		int nBola   = inv.contarItem(new Bola(0));

		qtyRapido.setText("x" + nRapido);
		qtyLento .setText("x" + nLento);
		qtyPeces .setText("x" + nPeces);
		qtyBola  .setText("x" + nBola);

		setSlotEnabled(slotRapido, nRapido > 0);
		setSlotEnabled(slotLento,  nLento > 0);
		// Passive slots: opacity only, never clickable
		slotPeces.setOpacity(nPeces > 0 ? 1.0 : 0.35);
		slotBola .setOpacity(nBola  > 0 ? 1.0 : 0.35);

		// Cancel selection if selected item is no longer available
		if ("Rapido".equals(selectedItem) && nRapido <= 0) clearSelection();
		if ("Lento" .equals(selectedItem) && nLento  <= 0) clearSelection();

		dadoResultText.setText("Turno de: " + pingu.getNom());
	}

	// -------------------------------------------------------
	// MOURE FITXA VISUAL
	// -------------------------------------------------------

	private void moverFichaVisual(int indiceFicha, int posicionNueva) {
		if (indiceFicha < 0 || indiceFicha >= fichas.length) return;

		Node ficha = fichas[indiceFicha];
		int oldPosition = posiciones[indiceFicha];
		posiciones[indiceFicha] = posicionNueva;

		int[] newPos = obtenerFilaColumna(posicionNueva);
		ficha.setTranslateX(0);
		ficha.setTranslateY(0);
		GridPane.setRowIndex(ficha, newPos[0]);
		GridPane.setColumnIndex(ficha, newPos[1]);

		redistribuirFichasEnPosicion(posicionNueva);
		redistribuirFichasEnPosicion(oldPosition);
	}

	/**
	 * Anima el moviment d'una fitxa cel·la per cel·la seguint el camí serp.
	 * Cada pas dura ~180ms i s'encadenen en una SequentialTransition.
	 */
	private void animarMovimientoCasillaPorCasilla(Node ficha, int from, int to, Runnable onFinished) {
		if (from == to) {
			if (onFinished != null) onFinished.run();
			return;
		}

		int direction = (to > from) ? 1 : -1;
		int steps = Math.abs(to - from);

		double cellWidth  = tablero.getWidth()  / this.columnas;
		double cellHeight = tablero.getHeight() / this.filas;

		SequentialTransition sequence = new SequentialTransition();

		for (int s = 0; s < steps; s++) {
			int currentCell = from + s * direction;
			int nextCell    = from + (s + 1) * direction;

			int[] currentRC = obtenerFilaColumna(currentCell);
			int[] nextRC    = obtenerFilaColumna(nextCell);

			double dx = (nextRC[1] - currentRC[1]) * cellWidth;
			double dy = (nextRC[0] - currentRC[0]) * cellHeight;

			TranslateTransition hop = new TranslateTransition(Duration.millis(180), ficha);
			hop.setByX(dx);
			hop.setByY(dy);

			final int nextCellFinal = nextCell;
			final int[] nextRCFinal = nextRC;
			hop.setOnFinished(e -> {
				// Snap piece to the grid cell after each hop
				ficha.setTranslateX(0);
				ficha.setTranslateY(0);
				GridPane.setRowIndex(ficha, nextRCFinal[0]);
				GridPane.setColumnIndex(ficha, nextRCFinal[1]);
			});

			sequence.getChildren().add(hop);
		}

		sequence.setOnFinished(e -> {
			if (onFinished != null) onFinished.run();
		});
		sequence.play();
	}

	// -------------------------------------------------------
	// LÒGICA POST-MOVIMENT: casella + guerra + foca
	// -------------------------------------------------------

	private void postMovimientoPinguino(Pinguino atacant, int indiceFichaAtacant) {
		Partida partida = gestorPartida.getPartida();
		Tablero t       = partida.getTablero();

		// 1) Casella especial
		Casilla casilla = t.getCasilla(atacant.getPos());
		if (casilla != null) {
			int posBefore = atacant.getPos();
			casilla.realizarAccion(partida, atacant);
			if (atacant.getPos() != posBefore) {
				int posModel = Math.max(0, Math.min(atacant.getPos(), t.getTamanyo() - 1));
				atacant.setPos(posModel);
				moverFichaVisual(indiceFichaAtacant, posModel);
			}
			// Mostrar l'event que ha passat si és una casella Evento
			if (casilla instanceof Evento) {
				Evento ev = (Evento) casilla;
				String desc = ev.getUltimoEventoDescripcion();
				if (desc != null && !desc.isEmpty()) {
					addEvent(atacant.getNom() + ": " + desc);
				}
			} else {
				if (casilla instanceof Agujero) {
					addEvent(atacant.getNom() + " ⚫ ¡Ha caído en un Agujero! ¡Retrocede!");
				} else if (casilla instanceof Oso) {
					if (atacant.getPos() == posBefore) {
						addEvent(atacant.getNom() + " 🐻 ¡Oso Polar! ¡Usó un Pez para escapar!");
					} else {
						addEvent(atacant.getNom() + " 🐻 ¡Oso Polar! ¡Vuelve al inicio!");
					}
				} else if (casilla instanceof SueloQuebradizo) {
					int totalItems = atacant.getInv().totalItems();
					if (totalItems > 5) {
						addEvent(atacant.getNom() + " 🧊 ¡Suelo agrietado! Demasiado peso, ¡vuelve al inicio!");
					} else if (totalItems > 0) {
						addEvent(atacant.getNom() + " 🧊 ¡Suelo agrietado! ¡Pierde el próximo turno!");
					} else {
						addEvent(atacant.getNom() + " 🧊 ¡Suelo agrietado! Inventario vacío, nada pasa.");
					}
				} else if (casilla instanceof Trineo) {
					addEvent(atacant.getNom() + " 🛷 ¡Trineo! ¡Avanza rápidamente!");
				} else if (!(casilla instanceof Normal)) {
					String tipusCasella = casilla.getClass().getSimpleName();
					addEvent(atacant.getNom() + " activa casella: " + tipusCasella);
				}
			}
		}

		// Actualitzar inventari immediatament
		actualizarInventarioUI();

		// 2) Comprovar si coincideix amb un altre pingüí → obrir PantallaGuerra
		for (int i = 0; i < partida.getJugadores().size(); i++) {
			Jugador altre = partida.getJugadores().get(i);
			if (altre == atacant) continue;
			if (!(altre instanceof Pinguino)) continue;
			Pinguino defensor = (Pinguino) altre;
			if (defensor.getPos() == atacant.getPos()) {
				final int indiceDefensor = i;
				obrirPantallaGuerra(defensor, indiceDefensor, atacant, indiceFichaAtacant, partida);
				return;
			}
		}

		// 3) Interacció amb la Foca si coincideix (només si foca activada)
		if (focaActivada && indiceFoca >= 0) {
			Jugador jFoca = partida.getJugadores().get(indiceFoca);
			if (jFoca instanceof Foca) {
				Foca foca = (Foca) jFoca;
				if (foca.getPos() == atacant.getPos()) {
					foca.aplastarJugador(partida, atacant);
					int posModel = Math.max(0, atacant.getPos());
					atacant.setPos(posModel);
					moverFichaVisual(indiceFichaAtacant, posModel);
					if (foca.isSoborno()) {
						addEvent("🦭 " + atacant.getNom() + " ¡ha sobornado a la Foca con un pez!");
					} else {
						addEvent("🦭 ¡La Foca ha atrapado a " + atacant.getNom() + "! → casilla " + posModel);
					}
					actualizarInventarioUI();
				}
			}
		}

		// 4) Comprovar guanyador (pingüí)
		comprovarGuanyadorPinguino(atacant);

		// 5) Avançar torn
		if (!partida.isFinalizada()) {
			avanzarAlSiguienteTurno();
		}
	}

	private void obrirPantallaGuerra(Pinguino defensor, int indiceDefensor,
	                                  Pinguino atacant, int indiceAtacant,
	                                  Partida partida) {
		try {
			FXMLLoader loader = new FXMLLoader(
				getClass().getResource("PantallaGuerra.fxml"));
			Parent root = loader.load();

			PantallaGuerra ctrl = loader.getController();
			ctrl.inicialitzar(defensor, atacant, partida, (defRet, atRet, passosDau, haEscapat) -> {
				if (haEscapat) {
					int novaPosD = Math.max(0, Math.min(
						defensor.getPos() + passosDau,
						partida.getTablero().getTamanyo() - 1));
					defensor.setPos(novaPosD);
					moverFichaVisual(indiceDefensor, novaPosD);
					addEvent(defensor.getNom() + " escapa lanzando el dado → " + passosDau + " casillas!");
				} else {
					if (defRet > 0) {
						int novaPosD = Math.max(0, defensor.getPos() - defRet);
						defensor.setPos(novaPosD);
						moverFichaVisual(indiceDefensor, novaPosD);
					}
					if (atRet > 0) {
						int novaPosA = Math.max(0, atacant.getPos() - atRet);
						atacant.setPos(novaPosA);
						moverFichaVisual(indiceAtacant, novaPosA);
					}
				}

				comprovarGuanyadorPinguino(defensor);
				comprovarGuanyadorPinguino(atacant);
				if (!partida.isFinalizada()) {
					avanzarAlSiguienteTurno();
				}
				actualizarInventarioUI();
			});

			ctrl.setAutoPlay(autoPlayOn);

			Stage guerraStage = new Stage();
			guerraStage.setTitle("⚔ ¡Batalla de Nieve!");
			guerraStage.initModality(Modality.APPLICATION_MODAL);
			guerraStage.initOwner(tablero.getScene().getWindow());
			Scene guerraScene = new Scene(root);
			CursorManager.apply(guerraScene);
			guerraStage.setScene(guerraScene);
			guerraStage.setResizable(false);
			guerraStage.setOnCloseRequest(ev -> ev.consume());
			guerraStage.iconifiedProperty().addListener((obs, wasMin, isMin) -> {
				if (isMin) guerraStage.setIconified(false);
			});
			guerraStage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
			if (!partida.isFinalizada()) {
				avanzarAlSiguienteTurno();
			}
		}
	}

	/** Comprova si un pingüí ha arribat al final. */
	private void comprovarGuanyadorPinguino(Pinguino pingu) {
		Partida partida = gestorPartida.getPartida();
		if (!partida.isFinalizada() && pingu.getPos() >= partida.getTablero().getTamanyo() - 1) {
			finalizarPartida(pingu.getNom(), partida);
		}
	}

	/** Comprova si la foca ha arribat al final. */
	private void comprovarGuanyadorFoca(Foca foca) {
		Partida partida = gestorPartida.getPartida();
		if (!partida.isFinalizada() && foca.getPos() >= partida.getTablero().getTamanyo() - 1) {
			finalizarPartida("🦭 Foca", partida);
		}
	}

	/** Finalitza la partida amb un guanyador. */
	private void finalizarPartida(String nomGuanyador, Partida partida) {
		partida.setFinalizada(true);
		stopAutoPlay();
		addEvent("🏆 " + nomGuanyador + " ¡ha ganado la partida!");
		dado.setDisable(true);
		clearSelection();
		setSlotEnabled(slotRapido, false);
		setSlotEnabled(slotLento,  false);
		setSlotEnabled(slotPeces,  false);

		java.sql.Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
		if (con != null) {
			try {
				if (partidaGuardadaId != -1) {
					GestorBBDD.marcarPartidaAcabadaConGanador(con, partidaGuardadaId, nomGuanyador);
					partidaGuardadaId = -1;
				} else {
					int savedId = guardarPartidaFinalitzada(con, partida);
					if (savedId > 0) GestorBBDD.marcarPartidaAcabadaConGanador(con, savedId, nomGuanyador);
				}
				if (!nomGuanyador.startsWith("🦭")) {
					GestorBBDD.incrementarPartidasGanadas(con, nomGuanyador);
				}
			} finally {
				GestorBBDD.cerrar(con);
			}
		}

		obrirPantallaFin(nomGuanyador, partida);
	}

	/** Saves the current game state to the DB and returns the new ID (-1 on error). */
	private int guardarPartidaFinalitzada(java.sql.Connection con, Partida partida) {
		try {
			Tablero t = partida.getTablero();
			int numCasillas = t.getTamanyo();

			StringBuilder sbCasillas = new StringBuilder();
			for (int i = 0; i < numCasillas; i++) {
				if (i > 0) sbCasillas.append(",");
				sbCasillas.append(t.getCasilla(i).getClass().getSimpleName());
			}

			int numJugadores = 0;
			for (Jugador j : partida.getJugadores()) {
				if (j instanceof Pinguino) numJugadores++;
			}

			String[] nombresPings    = new String[numJugadores];
			int[]    posicionesPings  = new int[numJugadores];
			String[][] inventariosPings = new String[numJugadores][];

			int idx = 0;
			for (Jugador j : partida.getJugadores()) {
				if (j instanceof Pinguino) {
					Pinguino p = (Pinguino) j;
					nombresPings[idx]    = p.getNom();
					posicionesPings[idx] = p.getPos();
					Inventario inv = p.getInv();
					java.util.List<Item> items = inv.getInv();
					String[] itemStrs = new String[items.size()];
					for (int k = 0; k < items.size(); k++) {
						Item item = items.get(k);
						itemStrs[k] = item.getNom() + ":" + item.getCantidad();
					}
					inventariosPings[idx] = itemStrs;
					idx++;
				}
			}

			int focaAct = focaActivada ? 1 : 0;
			int fPos = 0, fSoborno = 0, fTurnosBloq = 0;
			if (focaActivada && indiceFoca >= 0) {
				Foca foca = (Foca) partida.getJugadores().get(indiceFoca);
				fPos         = foca.getPos();
				fSoborno     = foca.isSoborno() ? 1 : 0;
				fTurnosBloq  = foca.getTurnosBloquejada();
			}

			String nomPartida = "Partida de " + nombreUsuarioLogueado;
			return GestorBBDD.guardarPartida(con, nombreUsuarioLogueado, nomPartida,
				numCasillas, sbCasillas.toString(),
				focaAct, fPos, fSoborno, fTurnosBloq,
				partida.getTurnos(), partida.getJugadorActual(),
				nombresPings, posicionesPings, inventariosPings);
		} catch (Exception e) {
			System.out.println("Error guardando partida finalizada: " + e.getMessage());
			return -1;
		}
	}

	// -------------------------------------------------------
	// PANTALLA DE FI DE PARTIDA
	// -------------------------------------------------------

	private void obrirPantallaFin(String nomGuanyador, Partida partida) {
		javafx.application.Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader(
						getClass().getResource("PantallaFin.fxml"));
				Parent root = loader.load();

				PantallaFin ctrl = loader.getController();
				ctrl.inicialitzar(nomGuanyador, partida.getTurnos());

				Stage finStage = new Stage();
				finStage.setTitle("🏆 ¡Fin de la Partida!");
				finStage.initModality(Modality.APPLICATION_MODAL);
				Scene finScene = new Scene(root);
				CursorManager.apply(finScene);
				finStage.setScene(finScene);
				finStage.setResizable(false);
				finStage.showAndWait();
				// showAndWait() ya retornó → el nested event loop cerró limpiamente

				if (!ctrl.isVolverAlMenu()) {
					javafx.application.Platform.exit();
					return;
				}

				Stage stage = (Stage) tablero.getScene().getWindow();
				FXMLLoader menuLoader = new FXMLLoader(
						getClass().getResource("PantallaMenu.fxml"));
				Parent menuRoot = menuLoader.load();
				PantallaMenu menuCtrl = menuLoader.getController();
				menuCtrl.setNombreUsuario(nombreUsuarioLogueado);
				Scene menuScene = new Scene(menuRoot);
				CursorManager.apply(menuScene);
				stage.setScene(menuScene);
				stage.setTitle("El Juego del Pingüino");
				menuCtrl.mostrarRanking();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// -------------------------------------------------------
	// TORN DE LA FOCA (CPU)
	// -------------------------------------------------------

	private void ejecutarTurnoFoca() {
		if (!focaActivada || indiceFoca < 0) return;

		Partida partida = gestorPartida.getPartida();
		Foca foca = (Foca) partida.getJugadores().get(indiceFoca);

		int posAntesFoca = foca.getPos();

		// Si la foca està bloquejada (sobornada)
		if (foca.isSoborno()) {
			foca.moverPosicio(0);
			int posDespreFoca = foca.getPos();
			int indiceFichaFoca = fichas.length - 1;
			moverFichaVisual(indiceFichaFoca, posDespreFoca);
			addEvent("🦭 La Foca está bloqueada " + foca.getTurnosBloquejada() + " turno(s) más.");
			return;
		}

		// IA: Buscar el pingüí més avançat
		Pinguino objectiu = null;
		int maxPos = -1;
		for (Jugador j : partida.getJugadores()) {
			if (j instanceof Pinguino) {
				Pinguino p = (Pinguino) j;
				if (p.getPos() > maxPos) {
					maxPos = p.getPos();
					objectiu = p;
				}
			}
		}

		Dado dadoFoca = new Dado("Normal", 1);
		int tirada = dadoFoca.tirar();

		int novaPosFoca;
		if (objectiu != null && objectiu.getPos() > posAntesFoca) {
			int distancia = objectiu.getPos() - posAntesFoca;
			if (distancia <= tirada) {
				novaPosFoca = objectiu.getPos();
			} else {
				novaPosFoca = posAntesFoca + tirada;
			}
		} else {
			novaPosFoca = posAntesFoca + tirada;
		}

		novaPosFoca = Math.min(novaPosFoca, partida.getTablero().getTamanyo() - 1);
		novaPosFoca = Math.max(0, novaPosFoca);
		foca.setPos(novaPosFoca);

		int indiceFichaFoca = fichas.length - 1;

		// Desactivar controls durant l'animació de la foca
		dado.setDisable(true);
		setSlotEnabled(slotRapido, false);
		setSlotEnabled(slotLento,  false);
		setSlotEnabled(slotPeces,  false);

		Node fichaFoca = fichas[indiceFichaFoca];
		int oldPosFoca = posiciones[indiceFichaFoca];
		posiciones[indiceFichaFoca] = novaPosFoca;

		final int posAntesFocaFinal = posAntesFoca;
		final int novaPosTemp = novaPosFoca;

		// Animate cell-by-cell like penguins
		animarMovimientoCasillaPorCasilla(fichaFoca, oldPosFoca, novaPosFoca, () -> {
			redistribuirFichasEnPosicion(novaPosTemp);
			redistribuirFichasEnPosicion(oldPosFoca);

			javafx.application.Platform.runLater(() -> {
				StringBuilder msg = new StringBuilder("🦭 La Foca se mueve a casilla " + novaPosTemp + ".");

				for (int i = 0; i < partida.getJugadores().size(); i++) {
					Jugador j = partida.getJugadores().get(i);
					if (!(j instanceof Pinguino)) continue;
					Pinguino pingu = (Pinguino) j;
					int posPingu = pingu.getPos();

					if (posPingu > posAntesFocaFinal && posPingu < novaPosTemp) {
						foca.golpearJugador(partida, pingu);
						msg.append(" ¡Ha pasado por encima de " + pingu.getNom() + " y le ha hecho perder la mitad de los ítems!");
					} else if (posPingu == novaPosTemp) {
						foca.aplastarJugador(partida, pingu);
						int posNova = Math.max(0, pingu.getPos());
						pingu.setPos(posNova);
						moverFichaVisual(i, posNova);
						if (foca.isSoborno()) {
							msg.append(" " + pingu.getNom() + " ¡ha sobornado a la Foca con un pez!");
						} else {
							msg.append(" ¡Ha atrapado a " + pingu.getNom() + "! → casilla " + posNova);
						}
					}
				}
				addEvent(msg.toString());
				actualizarInventarioUI();

				// Comprovar si la foca ha guanyat
				comprovarGuanyadorFoca(foca);
			});
		});
	}

	// -------------------------------------------------------
	// AVANÇAR AL PRÒXIM TORN
	// -------------------------------------------------------

	private void avanzarAlSiguienteTurno() {
		Partida partida = gestorPartida.getPartida();
		if (partida.isFinalizada()) return;

		clearSelection();
		turnosPinguinosEnRonda++;

		// Si tots els pingüins han jugat → torn de la foca (si activada)
		if (focaActivada && turnosPinguinosEnRonda >= totalPinguinos) {
			turnosPinguinosEnRonda = 0;
			ejecutarTurnoFoca();
			if (partida.isFinalizada()) return;
		} else if (!focaActivada && turnosPinguinosEnRonda >= totalPinguinos) {
			turnosPinguinosEnRonda = 0;
		}

		// Avançar al següent pingüí (saltant la foca)
		partida.siguienteTurno();

		// Saltar la foca en el cicle de torns (si activada)
		if (focaActivada && partida.getJugadorActual() == indiceFoca) {
			partida.siguienteTurno();
		}

		// Si el pròxim pingüí ha perdut el torn, el saltem
		// IMPORTANT: NO incrementar turnosPinguinosEnRonda — el torn perdut
		// compta com a torn del pingüí que ja va jugar, no com un torn nou
		Jugador seg = partida.getJugadores().get(partida.getJugadorActual());
		if (seg instanceof Pinguino) {
			Pinguino segP = (Pinguino) seg;
			if (!segP.isJuega()) {
				addEvent(segP.getNom() + " pierde el turno.");
				segP.setJuega(true);
				saltarTurnoPerdut();
				return;
			}
		}

		actualizarInventarioUI();
		marcarJugadorActual();
	}

	/**
	 * Salta un pingüí que ha perdut el torn SENSE incrementar el comptador de ronda.
	 * Això assegura que la foca només es mou després que l'últim pingüí hagi jugat realment.
	 */
	private void saltarTurnoPerdut() {
		Partida partida = gestorPartida.getPartida();
		if (partida.isFinalizada()) return;

		// Avançar al següent sense comptar com a torn de ronda
		partida.siguienteTurno();

		if (focaActivada && partida.getJugadorActual() == indiceFoca) {
			partida.siguienteTurno();
		}

		// Si el següent també ha perdut el torn, saltar-lo
		Jugador seg = partida.getJugadores().get(partida.getJugadorActual());
		if (seg instanceof Pinguino) {
			Pinguino segP = (Pinguino) seg;
			if (!segP.isJuega()) {
				addEvent(segP.getNom() + " pierde el turno.");
				segP.setJuega(true);
				saltarTurnoPerdut();
				return;
			}
		}

		actualizarInventarioUI();
		marcarJugadorActual();
	}

	// -------------------------------------------------------
	// LÒGICA COMUNA: tirar un dau concret
	// -------------------------------------------------------

	private void tirarDadoConcreto(Dado d) {
		Partida partida = gestorPartida.getPartida();
		if (partida.isFinalizada()) return;

		int indice = partida.getJugadorActual();
		if (focaActivada && indice == indiceFoca) return;

		Jugador j = partida.getJugadores().get(indice);
		if (!(j instanceof Pinguino)) return;
		Pinguino pingu = (Pinguino) j;

		int resultado = d.tirar();
		dadoResultText.setText("Ha salido: " + resultado);
		addEvent(pingu.getNom() + " tira " + d.getNom() + " → " + resultado);

		int novaPos = Math.max(0, Math.min(pingu.getPos() + resultado, partida.getTablero().getTamanyo() - 1));
		pingu.setPos(novaPos);

		// Desactivar controls durant l'animació
		isMoving = true;
		dado.setDisable(true);
		clearSelection();
		setSlotEnabled(slotRapido, false);
		setSlotEnabled(slotLento,  false);
		setSlotEnabled(slotPeces,  false);

		Node ficha = fichas[indice];
		int oldPosition = posiciones[indice];
		posiciones[indice] = novaPos;
		final int indiceFichaFinal = indice;

		// Build cell-by-cell sequential animation along the snake path
		animarMovimientoCasillaPorCasilla(ficha, oldPosition, novaPos, () -> {
			redistribuirFichasEnPosicion(novaPos);
			redistribuirFichasEnPosicion(oldPosition);
			javafx.application.Platform.runLater(() -> {
				isMoving = false;
				postMovimientoPinguino(pingu, indiceFichaFinal);
			});
		});
	}

	// -------------------------------------------------------
	// HANDLERS DELS BOTONS
	// -------------------------------------------------------

	@FXML
	private void handleDado(ActionEvent event) {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		Pinguino pingu = (Pinguino) partida.getJugadores().get(indice);
		Dado d = new Dado("Normal", 1);
		tirarDadoConcreto(d);
	}

	@FXML
	private void handleSelectRapido() {
		if (slotRapido.getOpacity() < 0.5) return;
		if ("Rapido".equals(selectedItem)) { clearSelection(); return; }
		selectedItem = "Rapido";
		setSlotSelected(slotRapido, true);
		setSlotSelected(slotLento,  false);
		usarBox.setVisible(true);
	}

	@FXML
	private void handleSelectLento() {
		if (slotLento.getOpacity() < 0.5) return;
		if ("Lento".equals(selectedItem)) { clearSelection(); return; }
		selectedItem = "Lento";
		setSlotSelected(slotRapido, false);
		setSlotSelected(slotLento,  true);
		usarBox.setVisible(true);
	}

	@FXML
	private void handleUsarItem() {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		Pinguino pingu = (Pinguino) partida.getJugadores().get(indice);
		if ("Rapido".equals(selectedItem) && pingu.getInv().contarItem(new Dado("Rapido", 0)) > 0) {
			pingu.quitarItem(new Dado("Rapido", 0));
			clearSelection();
			tirarDadoConcreto(new Dado("Rapido", 1));
		} else if ("Lento".equals(selectedItem) && pingu.getInv().contarItem(new Dado("Lento", 0)) > 0) {
			pingu.quitarItem(new Dado("Lento", 0));
			clearSelection();
			tirarDadoConcreto(new Dado("Lento", 1));
		}
	}

	// -------------------------------------------------------
	// AUTO-PLAY
	// -------------------------------------------------------

	@FXML
	private void handleAutoPlay() {
		autoPlayOn = !autoPlayOn;
		if (autoPlayOn) {
			btnAutoPlay.setText("Auto: ON");
			btnAutoPlay.getStyleClass().add("active");
			dado.setDisable(true);
			autoPlayTimeline = new Timeline(new KeyFrame(Duration.millis(1500), e -> {
				if (gestorPartida != null
						&& !gestorPartida.getPartida().isFinalizada()
						&& !isMoving) {
					handleDado(null);
				}
			}));
			autoPlayTimeline.setCycleCount(Animation.INDEFINITE);
			autoPlayTimeline.play();
		} else {
			stopAutoPlay();
		}
	}

	private void stopAutoPlay() {
		autoPlayOn = false;
		if (autoPlayTimeline != null) {
			autoPlayTimeline.stop();
			autoPlayTimeline = null;
		}
		if (btnAutoPlay != null) {
			btnAutoPlay.setText("Auto: OFF");
			btnAutoPlay.getStyleClass().remove("active");
		}
		if (dado != null && !isMoving) {
			dado.setDisable(false);
		}
	}

	// -------------------------------------------------------
	// HANDLERS DEL MENÚ
	// -------------------------------------------------------

	@FXML private void handleNewGame() {
		try {
			Stage stage = (Stage) tablero.getScene().getWindow();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaMenu.fxml"));
			Parent root = loader.load();
			PantallaMenu menuCtrl = loader.getController();
			menuCtrl.setNombreUsuario(nombreUsuarioLogueado);
			Scene menuScene = new Scene(root);
			CursorManager.apply(menuScene);
			stage.setScene(menuScene);
			stage.setTitle("El Juego del Pingüino");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML private void handleSaveGame() {
		Partida partida = gestorPartida.getPartida();
		if (partida == null) {
			addEvent("⚠ No hay partida para guardar.");
			return;
		}
		if (partida.isFinalizada()) {
			addEvent("⚠ La partida ya ha finalizado, no se puede guardar.");
			return;
		}

		// Ask the user for a save name
		TextInputDialog dialog = new TextInputDialog("Partida de " + nombreUsuarioLogueado);
		dialog.setTitle("Guardar Partida");
		dialog.setHeaderText(null);
		dialog.setContentText("Nombre de la partida:");
		java.util.Optional<String> result = dialog.showAndWait();
		if (!result.isPresent()) return; // User cancelled
		String nomPartida = result.get().trim();
		if (nomPartida.isEmpty()) nomPartida = "Partida";

		java.sql.Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
		if (con == null) {
			addEvent("❌ Error conectando a la base de datos.");
			return;
		}
		try {
			Tablero t = partida.getTablero();
			int numCasillas = t.getTamanyo();

			// Serialitzar tipus de caselles
			StringBuilder sbCasillas = new StringBuilder();
			for (int i = 0; i < numCasillas; i++) {
				if (i > 0) sbCasillas.append(",");
				sbCasillas.append(t.getCasilla(i).getClass().getSimpleName());
			}

			// Comptar pingüins (excloent foca)
			int numJugadores = 0;
			for (Jugador j : partida.getJugadores()) {
				if (j instanceof Pinguino) numJugadores++;
			}

			// Construir arrays de pingüins i inventaris
			String[] nombresPings = new String[numJugadores];
			int[] posicionesPings = new int[numJugadores];
			String[][] inventariosPings = new String[numJugadores][];

			int idx = 0;
			for (Jugador j : partida.getJugadores()) {
				if (j instanceof Pinguino) {
					Pinguino p = (Pinguino) j;
					nombresPings[idx] = p.getNom();
					posicionesPings[idx] = p.getPos();
					Inventario inv = p.getInv();
					java.util.List<Item> items = inv.getInv();
					String[] itemStrs = new String[items.size()];
					for (int k = 0; k < items.size(); k++) {
						Item item = items.get(k);
						itemStrs[k] = item.getNom() + ":" + item.getCantidad();
					}
					inventariosPings[idx] = itemStrs;
					idx++;
				}
			}

			// Dades de la foca
			int focaAct = focaActivada ? 1 : 0;
			int fPos = 0;
			int fSoborno = 0;
			int fTurnosBloq = 0;
			if (focaActivada && indiceFoca >= 0) {
				Foca foca = (Foca) partida.getJugadores().get(indiceFoca);
				fPos = foca.getPos();
				fSoborno = foca.isSoborno() ? 1 : 0;
				fTurnosBloq = foca.getTurnosBloquejada();
			}

			int newId = GestorBBDD.guardarPartida(con, nombreUsuarioLogueado, nomPartida,
				numCasillas, sbCasillas.toString(),
				focaAct, fPos, fSoborno, fTurnosBloq,
				partida.getTurnos(), partida.getJugadorActual(),
				nombresPings, posicionesPings, inventariosPings);

			if (newId > 0) {
				this.partidaGuardadaId = newId;
				GestorBBDD.guardarEvents(con, newId, new ArrayList<>(eventosData));
				addEvent("💾 ¡Partida '" + nomPartida + "' guardada correctamente!");
			} else {
				addEvent("❌ Error al guardar la partida.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			addEvent("❌ Error al guardar: " + e.getMessage());
		} finally {
			GestorBBDD.cerrar(con);
		}
	}

	@FXML private void handleLoadGame() {
		java.sql.Connection con = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
		if (con == null) {
			addEvent("❌ Error conectando a la base de datos.");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaCargarPartida.fxml"));
			Parent root = loader.load();
			PantallaCargarPartida ctrl = loader.getController();
			ctrl.inicialitzar(con, nombreUsuarioLogueado);

			Stage selStage = new Stage();
			selStage.setTitle("Cargar Partida");
			selStage.initModality(Modality.APPLICATION_MODAL);
			Scene selScene = new Scene(root, 720, 460);
			CursorManager.apply(selScene);
			selStage.setScene(selScene);
			selStage.setResizable(true);
			selStage.showAndWait();

			if (ctrl.isLoaded()) {
				java.util.LinkedHashMap<String, String> datos = ctrl.getSelectedPartida();
				restaurarPartida(datos);
			}
		} catch (Exception e) {
			e.printStackTrace();
			addEvent("❌ Error al cargar: " + e.getMessage());
		} finally {
			GestorBBDD.cerrar(con);
		}
	}

	/**
	 * Restaura la partida a partir de les dades carregades de la BBDD.
	 */
	public void restaurarPartida(java.util.LinkedHashMap<String, String> datos) {
		// Guardar l'ID de la BBDD per poder eliminar-la quan acabi la partida
		String idStr = datos.get("ID");
		partidaGuardadaId = (idStr != null) ? Integer.parseInt(idStr) : -1;

		int numCasillas = Integer.parseInt(datos.get("NUM_CASILLAS"));
		String[] tiposCasillas = datos.get("CASILLAS_TIPOS").split(",");
		int numJugadores = Integer.parseInt(datos.get("NUM_JUGADORES"));
		String[] nombres = datos.get("NOMBRES_JUGADORES").split(",");
		String[] posStr = datos.get("POSICIONES").split(",");
		String invData = datos.get("INVENTARIOS");
		boolean ambFoca = "1".equals(datos.get("FOCA_ACTIVADA"));
		int focaPosDB = Integer.parseInt(datos.getOrDefault("FOCA_POS", "0"));
		boolean focaSobornoDB = "1".equals(datos.get("FOCA_SOBORNO"));
		int focaTurnosBloqDB = Integer.parseInt(datos.getOrDefault("FOCA_TURNOS_BLOQ", "0"));
		int turnosDB = Integer.parseInt(datos.getOrDefault("TURNOS", "0"));
		int jugadorActualDB = Integer.parseInt(datos.getOrDefault("JUGADOR_ACTUAL", "0"));

		// 1) Reconstruir el Tablero amb els tipus exactes
		Tablero t = new Tablero(numCasillas);
		// Substituir les caselles generades aleatòriament pels tipus guardats
		ArrayList<Casilla> casillasGuardadas = new ArrayList<>();
		for (int i = 0; i < tiposCasillas.length; i++) {
			String tipo = tiposCasillas[i].trim();
			switch (tipo) {
				case "Oso": casillasGuardadas.add(new Oso(i)); break;
				case "Agujero": casillasGuardadas.add(new Agujero(i)); break;
				case "Trineo": casillasGuardadas.add(new Trineo(i)); break;
				case "Evento": casillasGuardadas.add(new Evento(i)); break;
				case "SueloQuebradizo": casillasGuardadas.add(new SueloQuebradizo(i)); break;
				default: casillasGuardadas.add(new Normal(i)); break;
			}
		}
		t.setCasillas(casillasGuardadas);

		// 2) Reconstruir jugadors
		String[] inventarisParts = (invData != null && !invData.isEmpty()) ? invData.split(";", -1) : new String[numJugadores];
		String[] colors = {"Azul", "Rojo", "Verde", "Amarillo"};

		ArrayList<Jugador> jugadors = new ArrayList<>();
		for (int i = 0; i < numJugadores; i++) {
			int pos = Integer.parseInt(posStr[i].trim());
			ArrayList<Item> items = new ArrayList<>();
			// Deserialitzar inventari
			if (inventarisParts != null && i < inventarisParts.length && inventarisParts[i] != null && !inventarisParts[i].trim().isEmpty()) {
				String[] itemParts = inventarisParts[i].split(",");
				for (String itemStr : itemParts) {
					String[] kv = itemStr.split(":");
					if (kv.length == 2) {
						String itemNom = kv[0].trim();
						int itemCant = Integer.parseInt(kv[1].trim());
						switch (itemNom) {
							case "Normal": case "Rapido": case "Lento":
								items.add(new Dado(itemNom, itemCant));
								break;
							case "Pez":
								items.add(new Pez(itemCant));
								break;
							case "Bola":
								items.add(new Bola(itemCant));
								break;
						}
					}
				}
			}
			// Assegurar que hi ha almenys un dado Normal
			boolean teDadoNormal = false;
			for (Item it : items) {
				if (it.getNom().equals("Normal")) { teDadoNormal = true; break; }
			}
			if (!teDadoNormal) items.add(0, new Dado("Normal", 1));

			Pinguino p = new Pinguino(nombres[i].trim(), pos, colors[i % colors.length], new Inventario(items));
			p.setJuega(true);
			jugadors.add(p);
		}

		// 3) Foca
		this.focaActivada = ambFoca;
		if (ambFoca) {
			Foca foca = new Foca(focaPosDB);
			if (focaSobornoDB) {
				foca.esSobornado(null, null);
				foca.setTurnosBloquejada(focaTurnosBloqDB);
			}
			jugadors.add(foca);
			this.indiceFoca = jugadors.size() - 1;
		} else {
			this.indiceFoca = -1;
		}

		// 4) Configurar gestorPartida
		this.totalPinguinos = numJugadores;
		gestorPartida = new GestorPartida();
		gestorPartida.nuevaPartida(t, jugadors);
		Partida partida = gestorPartida.getPartida();
		partida.setTurnos(turnosDB);
		partida.setJugadorActual(jugadorActualDB);

		// 5) Configurar el taulell visual
		this.columnas = (int) Math.ceil(Math.sqrt(t.getTamanyo()));
		this.filas = (int) Math.ceil((double) t.getTamanyo() / this.columnas);

		tablero.getColumnConstraints().clear();
		tablero.getRowConstraints().clear();
		for (int i = 0; i < this.columnas; i++) {
			javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
			cc.setPercentWidth(100.0 / this.columnas);
			tablero.getColumnConstraints().add(cc);
		}
		for (int i = 0; i < this.filas; i++) {
			javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
			rc.setPercentHeight(100.0 / this.filas);
			tablero.getRowConstraints().add(rc);
		}

		// 6) Configurar fitxes visuals
		Canvas[] totesLesFitxes = {P1, P2, P3, P4};
		for (Canvas c : totesLesFitxes) c.setVisible(false);

		if (ambFoca) {
			fichas = new Node[numJugadores + 1];
			posiciones = new int[numJugadores + 1];
			for (int i = 0; i < numJugadores; i++) {
				fichas[i] = totesLesFitxes[i];
				totesLesFitxes[i].setVisible(true);
				posiciones[i] = jugadors.get(i).getPos();
				PinguinoRenderer.draw(totesLesFitxes[i].getGraphicsContext2D(),
					PinguinoRenderer.GAME_PX,
					PinguinoRenderer.DEFAULT_COLORS[i % PinguinoRenderer.DEFAULT_COLORS.length],
					false);
			}
			fichas[numJugadores] = P5;
			P5.setVisible(true);
			posiciones[numJugadores] = focaPosDB;
			PinguinoRenderer.draw(P5.getGraphicsContext2D(), PinguinoRenderer.GAME_PX, null, true);
		} else {
			fichas = new Node[numJugadores];
			posiciones = new int[numJugadores];
			for (int i = 0; i < numJugadores; i++) {
				fichas[i] = totesLesFitxes[i];
				totesLesFitxes[i].setVisible(true);
				posiciones[i] = jugadors.get(i).getPos();
				PinguinoRenderer.draw(totesLesFitxes[i].getGraphicsContext2D(),
					PinguinoRenderer.GAME_PX,
					PinguinoRenderer.DEFAULT_COLORS[i % PinguinoRenderer.DEFAULT_COLORS.length],
					false);
			}
			P5.setVisible(false);
		}

		// 7) Posicionar fitxes al taulell
		for (int i = 0; i < fichas.length; i++) {
			int[] pos = obtenerFilaColumna(posiciones[i]);
			GridPane.setRowIndex(fichas[i], pos[0]);
			GridPane.setColumnIndex(fichas[i], pos[1]);
			GridPane.setHalignment(fichas[i], javafx.geometry.HPos.CENTER);
			GridPane.setValignment(fichas[i], javafx.geometry.VPos.CENTER);
			GridPane.setMargin(fichas[i], javafx.geometry.Insets.EMPTY);
		}

		// 8) Netejar textos de caselles anteriors i redibuixar
		tablero.getChildren().removeIf(node ->
			TAG_CASILLA_TEXT.equals(node.getUserData())
		);

		mostrarTiposDeCasillasEnTablero(t);

		for (int i = 0; i < fichas.length; i++) {
			redistribuirFichasEnPosicion(posiciones[i]);
		}

		actualizarInventarioUI();
		marcarJugadorActual();
		this.turnosPinguinosEnRonda = 0;
		this.jocIniciat = true;

		// Load saved events for this partida
		eventosData.clear();
		if (partidaGuardadaId > 0) {
			java.sql.Connection conEvts = GestorBBDD.conectarBBDD("fuera", "DW2526_GR02_PINGU", "ACOMRDT");
			if (conEvts != null) {
				ArrayList<String> evts = GestorBBDD.carregarEvents(conEvts, partidaGuardadaId);
				GestorBBDD.cerrar(conEvts);
				eventosData.setAll(evts);
				if (!evts.isEmpty()) eventosListView.scrollTo(evts.size() - 1);
			}
		}
		addEvent("📂 ¡Partida cargada correctamente!");

		javafx.application.Platform.runLater(this::drawPath);

		if (!focaActivada) {
			setSlotEnabled(slotPeces, false);
		}
	}

	@FXML private void handleQuitGame() { System.exit(0); }

	public void setGestorPartida(GestorPartida gestorPartida) {
		this.gestorPartida = gestorPartida;
	}
}
