package VISTAS;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import GESTORES.GestorPartida;
import MODELOS.*;

public class PantallaJuego {

	// Menu items
	@FXML private MenuItem newGame;
	@FXML private MenuItem saveGame;
	@FXML private MenuItem loadGame;
	@FXML private MenuItem quitGame;

	// Buttons
	@FXML private Button dado;
	@FXML private Button rapido;
	@FXML private Button lento;
	@FXML private Button peces;
	@FXML private Button nieve;

	// Texts
	@FXML private Text dadoResultText;
	@FXML private Text rapido_t;
	@FXML private Text lento_t;
	@FXML private Text peces_t;
	@FXML private Text nieve_t;
	@FXML private Text eventos;

	// Game board and player pieces (4 pingüinos + 1 foca)
	@FXML private GridPane tablero;
	@FXML private Circle P1;
	@FXML private Circle P2;
	@FXML private Circle P3;
	@FXML private Circle P4;
	@FXML private Circle P5;

	private GestorPartida gestorPartida;
	private int columnas;
	private int filas;

	private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";
	private final Random rand = new Random();

	// posiciones[0..3] = pingüinos, posiciones[4] = foca
	private int[] posiciones = {0, 0, 0, 0, 0};
	private Node[] fichas;

	// Índex de la foca dins la llista de jugadors de la partida
	private int indiceFoca = -1;

	// Mode de dificultat: true = imposible (amb foca), false = normal (sense foca)
	private boolean modoImposible = true;

	// Nom del jugador logejat (passat des del menú)
	private String nombreUsuarioLogueado = "Jugador";

	private boolean jocIniciat = false;

	/**
	 * Configura la dificultat i inicia el joc. Es crida des de PantallaMenu
	 * DESPRÉS que l'FXML s'hagi carregat.
	 */
	public void setModoImposible(boolean imposible) {
		this.modoImposible = imposible;
		if (!jocIniciat) {
			iniciarJoc();
			jocIniciat = true;
		}
	}

	public void setNombreUsuario(String nom) {
		this.nombreUsuarioLogueado = nom;
	}

	@FXML
	private void initialize() {
		// L'FXML s'ha carregat. Esperem a setModoImposible() per iniciar el joc.
	}

	/**
	 * Inicialitza el joc amb la dificultat configurada.
	 * Es crida des de setModoImposible().
	 */
	private void iniciarJoc() {
		eventos.setText("¡El juego ha comenzado!");

		int totalCasillas = 50;
		Tablero t = new Tablero(totalCasillas);

		this.columnas = (int) Math.ceil(Math.sqrt(totalCasillas));
		this.filas    = (int) Math.ceil((double) totalCasillas / this.columnas);

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

		// 4 pingüinos
		String[] noms   = {"Jugador1", "Jugador2", "Jugador3", "Jugador4"};
		String[] colors = {"Azul", "Rojo", "Amarillo", "Verde"};
		for (int i = 0; i < 4; i++) {
			ArrayList<Item> items = new ArrayList<>();
			items.add(new Dado("Normal", 1));
			jugadors.add(new Pinguino(noms[i], 0, colors[i], new Inventario(items)));
		}

		// Foca (CPU) — nivell impossible
		if (modoImposible) {
			Foca foca = new Foca(0);
			jugadors.add(foca);
			indiceFoca = jugadors.size() - 1;
		}

		gestorPartida.nuevaPartida(t, jugadors);

		// fichas[0..3] = pingüinos, fichas[4] = foca (P5)
		if (modoImposible) {
			fichas = new Node[]{P1, P2, P3, P4, P5};
			posiciones = new int[]{0, 0, 0, 0, 0};
		} else {
			fichas = new Node[]{P1, P2, P3, P4};
			posiciones = new int[]{0, 0, 0, 0};
			// Amagar la foca si existeix
			if (P5 != null) {
				P5.setVisible(false);
			}
		}

		int[] margenesIzq = modoImposible
				? new int[]{0, 26, 52, 78, 0}
				: new int[]{0, 34, 68, 0};

		for (int i = 0; i < fichas.length; i++) {
			GridPane.setRowIndex(fichas[i], 0);
			GridPane.setColumnIndex(fichas[i], 0);
			GridPane.setMargin(fichas[i], new javafx.geometry.Insets(
					(i == fichas.length - 1 && modoImposible) ? 28 : 0,
					0, 0, margenesIzq[i]));
		}

		Text start = new Text("Start");
		start.getStyleClass().add("cell-title");
		GridPane.setRowIndex(start, 0);
		GridPane.setColumnIndex(start, 0);
		GridPane.setHalignment(start, javafx.geometry.HPos.CENTER);
		GridPane.setValignment(start, javafx.geometry.VPos.CENTER);
		tablero.getChildren().add(start);

		Text finish = new Text("Finish");
		finish.getStyleClass().add("cell-title");
		int[] posFinish = obtenerFilaColumna(totalCasillas - 1);
		GridPane.setRowIndex(finish, posFinish[0]);
		GridPane.setColumnIndex(finish, posFinish[1]);
		GridPane.setHalignment(finish, javafx.geometry.HPos.CENTER);
		GridPane.setValignment(finish, javafx.geometry.VPos.CENTER);
		tablero.getChildren().add(finish);

		mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
		actualizarInventarioUI();
		marcarJugadorActual();
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
		for (int i = 1; i < totalCasillas - 1; i++) {
			Casilla casilla = t.getCasillas().get(i);
			String tipo = casilla.getClass().getSimpleName();
			Text texto = new Text(tipo);
			texto.setUserData(TAG_CASILLA_TEXT);
			texto.getStyleClass().add("cell-type");
			int[] pos = obtenerFilaColumna(i);
			GridPane.setRowIndex(texto, pos[0]);
			GridPane.setColumnIndex(texto, pos[1]);
			GridPane.setHalignment(texto, javafx.geometry.HPos.CENTER);
			GridPane.setValignment(texto, javafx.geometry.VPos.CENTER);
			tablero.getChildren().add(texto);
		}
	}

	// -------------------------------------------------------
	// MARCAR JUGADOR ACTUAL (indicador visual)
	// -------------------------------------------------------

	private void marcarJugadorActual() {
		// Treiem la classe current-player de totes les fitxes
		for (Node ficha : fichas) {
			ficha.getStyleClass().remove("current-player");
		}
		// Afegim la classe al jugador actual (si és un pingüí)
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		if (indice < fichas.length && indice != indiceFoca) {
			fichas[indice].getStyleClass().add("current-player");
		}
	}

	// -------------------------------------------------------
	// ACTUALITZAR UI INVENTARI
	// -------------------------------------------------------

	private void actualizarInventarioUI() {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		if (indice == indiceFoca) {
			// Torn de la foca: deshabilitar tots els botons
			dado.setDisable(true);
			rapido.setDisable(true);
			lento.setDisable(true);
			peces.setDisable(true);
			nieve.setDisable(true);
			dadoResultText.setText("Torn de la Foca (CPU)");
			return;
		}

		Jugador j = partida.getJugadores().get(indice);
		if (!(j instanceof Pinguino)) return;
		Pinguino pingu = (Pinguino) j;
		Inventario inv = pingu.getInv();

		dado.setDisable(false);

		int nRapido = inv.contarItem(new Dado("Rapido", 0));
		int nLento  = inv.contarItem(new Dado("Lento", 0));
		int nPeces  = inv.contarItem(new Pez(0));
		int nNieve  = inv.contarItem(new Bola(0));

		rapido.setDisable(nRapido <= 0);
		lento.setDisable(nLento <= 0);
		peces.setDisable(nPeces <= 0);
		nieve.setDisable(nNieve <= 0);

		rapido_t.setText("x" + nRapido);
		lento_t.setText("x"  + nLento);
		peces_t.setText("x"  + nPeces);
		nieve_t.setText("x"  + nNieve);
	}

	// -------------------------------------------------------
	// MÒDUL DE MOVIMENT AMB ANIMACIÓ
	// -------------------------------------------------------

	private void moverJugador(int indiceFicha, int posicionModeloNueva) {
		if (indiceFicha < 0 || indiceFicha >= fichas.length) return;

		Node ficha = fichas[indiceFicha];
		int oldPosition = posiciones[indiceFicha];
		posiciones[indiceFicha] = posicionModeloNueva;

		int[] oldPos = obtenerFilaColumna(oldPosition);
		int[] newPos = obtenerFilaColumna(posicionModeloNueva);

		double cellWidth  = tablero.getWidth()  / this.columnas;
		double cellHeight = tablero.getHeight() / this.filas;

		double dx = (newPos[1] - oldPos[1]) * cellWidth;
		double dy = (newPos[0] - oldPos[0]) * cellHeight;

		TranslateTransition slide = new TranslateTransition(Duration.millis(350), ficha);
		slide.setByX(dx);
		slide.setByY(dy);
		slide.setOnFinished(e -> {
			ficha.setTranslateX(0);
			ficha.setTranslateY(0);
			GridPane.setRowIndex(ficha, newPos[0]);
			GridPane.setColumnIndex(ficha, newPos[1]);
			dado.setDisable(false);
		});
		slide.play();
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
				posiciones[indiceFichaAtacant] = posModel;
				int[] newPos = obtenerFilaColumna(posModel);
				GridPane.setRowIndex(fichas[indiceFichaAtacant], newPos[0]);
				GridPane.setColumnIndex(fichas[indiceFichaAtacant], newPos[1]);
				// Mostrar quin tipus de casella ha activat
				String tipusCasella = casilla.getClass().getSimpleName();
				eventos.setText(atacant.getNom() + " activa casella: " + tipusCasella + " → posició " + posModel);
			}
		}

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

		// 3) Interacció amb la Foca si coincideix — NIVELL IMPOSSIBLE
		if (modoImposible && indiceFoca >= 0) {
			Jugador jFoca = partida.getJugadores().get(indiceFoca);
			if (jFoca instanceof Foca) {
				Foca foca = (Foca) jFoca;
				if (foca.getPos() == atacant.getPos()) {
					foca.golpearJugador(partida, atacant);
					int posModel = Math.max(0, atacant.getPos());
					atacant.setPos(posModel);
					posiciones[indiceFichaAtacant] = posModel;
					int[] np = obtenerFilaColumna(posModel);
					GridPane.setRowIndex(fichas[indiceFichaAtacant], np[0]);
					GridPane.setColumnIndex(fichas[indiceFichaAtacant], np[1]);
					eventos.setText("🦭 La Foca ha colpejat " + atacant.getNom() + "!");
				}
			}
		}

		// 4) Comprovar guanyador
		comprovarGuanyador(atacant);

		// 5) Avançar torn (si no hi ha hagut guerra ni final de partida)
		if (!partida.isFinalizada()) {
			avanzarAlSiguienteTurno();
		}
	}

	/**
	 * Obre la finestra modal de PantallaGuerra.
	 */
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
					posiciones[indiceDefensor] = novaPosD;
					int[] np = obtenerFilaColumna(novaPosD);
					GridPane.setRowIndex(fichas[indiceDefensor], np[0]);
					GridPane.setColumnIndex(fichas[indiceDefensor], np[1]);
					eventos.setText(defensor.getNom() + " escapa llançant el dau → " + passosDau + " caselles!");

				} else {
					if (defRet > 0) {
						int novaPosD = Math.max(0, defensor.getPos() - defRet);
						defensor.setPos(novaPosD);
						posiciones[indiceDefensor] = novaPosD;
						int[] np = obtenerFilaColumna(novaPosD);
						GridPane.setRowIndex(fichas[indiceDefensor], np[0]);
						GridPane.setColumnIndex(fichas[indiceDefensor], np[1]);
					}
					if (atRet > 0) {
						int novaPosA = Math.max(0, atacant.getPos() - atRet);
						atacant.setPos(novaPosA);
						posiciones[indiceAtacant] = novaPosA;
						int[] np = obtenerFilaColumna(novaPosA);
						GridPane.setRowIndex(fichas[indiceAtacant], np[0]);
						GridPane.setColumnIndex(fichas[indiceAtacant], np[1]);
					}
				}

				comprovarGuanyador(defensor);
				comprovarGuanyador(atacant);
				if (!partida.isFinalizada()) {
					avanzarAlSiguienteTurno();
				}
				actualizarInventarioUI();
			});

			Stage guerraStage = new Stage();
			guerraStage.setTitle("⚔ Batalla de Neu");
			guerraStage.initModality(Modality.APPLICATION_MODAL);
			guerraStage.setScene(new Scene(root));
			guerraStage.setResizable(false);
			guerraStage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
			if (!partida.isFinalizada()) {
				avanzarAlSiguienteTurno();
			}
		}
	}

	/** Comprova si un pingüí ha arribat al final i marca la partida com a guanyada. */
	private void comprovarGuanyador(Pinguino pingu) {
		Partida partida = gestorPartida.getPartida();
		if (!partida.isFinalizada() && pingu.getPos() >= partida.getTablero().getTamanyo() - 1) {
			partida.setFinalizada(true);
			partida.setGanador(pingu);
			eventos.setText("🏆 " + pingu.getNom() + " ha guanyat la partida!");
			dado.setDisable(true);
			rapido.setDisable(true);
			lento.setDisable(true);
			peces.setDisable(true);
			nieve.setDisable(true);

			// Obrir pantalla de fi
			obrirPantallaFin(pingu, partida);
		}
	}

	// -------------------------------------------------------
	// PANTALLA DE FI DE PARTIDA
	// -------------------------------------------------------

	private void obrirPantallaFin(Pinguino guanyador, Partida partida) {
		javafx.application.Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader(
						getClass().getResource("PantallaFin.fxml"));
				Parent root = loader.load();

				PantallaFin ctrl = loader.getController();
				ctrl.inicialitzar(guanyador.getNom(), partida.getTurnos(),
						modoImposible ? "Imposible" : "Normal");

				Stage finStage = new Stage();
				finStage.setTitle("🏆 Fi de la Partida");
				finStage.initModality(Modality.APPLICATION_MODAL);
				finStage.setScene(new Scene(root));
				finStage.setResizable(false);
				finStage.showAndWait();

				// Després de tancar, tornar al menú
				if (ctrl.isVolverAlMenu()) {
					Stage stage = (Stage) tablero.getScene().getWindow();
					FXMLLoader menuLoader = new FXMLLoader(
							getClass().getResource("PantallaMenu.fxml"));
					Parent menuRoot = menuLoader.load();
					stage.setScene(new Scene(menuRoot));
					stage.setTitle("El Juego del Pingüino");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// -------------------------------------------------------
	// TORN DE LA FOCA (CPU) — NIVELL IMPOSSIBLE (IA MILLORADA)
	// -------------------------------------------------------

	/**
	 * IA millorada de la Foca:
	 * - Persegueix el pingüí MÉS avançat (el que està més a prop del final).
	 * - Si pot atrapar-lo (distància <= tirada), tira per intentar arribar-hi.
	 * - Si passa per sobre d'un pingüí, el colpeja.
	 * - Si coincideix, li roba la meitat de l'inventari.
	 */
	private void ejecutarTurnoFoca() {
		Partida partida = gestorPartida.getPartida();
		Foca foca = (Foca) partida.getJugadores().get(indiceFoca);

		int posAntesFoca = foca.getPos();

		// Comprovar si la foca està bloquejada
		if (foca.isSoborno()) {
			foca.moverPosicio(0); // Descompta el bloqueig internament
			int posDespreFoca = foca.getPos();

			// Sincronitzar posició visual
			int indiceFichaFoca = fichas.length - 1; // Última fitxa = foca
			posiciones[indiceFichaFoca] = posDespreFoca;
			int[] np = obtenerFilaColumna(posDespreFoca);
			GridPane.setRowIndex(fichas[indiceFichaFoca], np[0]);
			GridPane.setColumnIndex(fichas[indiceFichaFoca], np[1]);

			eventos.setText("🦭 La Foca està bloquejada " + foca.getTurnosBloquejada() + " torn(s) més.");
			return;
		}

		// IA: Buscar el pingüí més avançat per perseguir-lo
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

		// La foca tira el dau normal
		Dado dadoFoca = new Dado("Normal", 1);
		int tirada = dadoFoca.tirar();

		// Si l'objectiu està a prop, la foca intenta arribar-hi
		int novaPosFoca;
		if (objectiu != null && objectiu.getPos() > posAntesFoca) {
			// Perseguir: moure's sempre cap al pingüí objectiu
			int distancia = objectiu.getPos() - posAntesFoca;
			if (distancia <= tirada) {
				novaPosFoca = objectiu.getPos(); // Arribar exactament
			} else {
				novaPosFoca = posAntesFoca + tirada; // Avançar el màxim
			}
		} else {
			// Si tots els pingüins estan darrere, avançar normalment
			novaPosFoca = posAntesFoca + tirada;
		}

		// Limitar al taulell
		novaPosFoca = Math.min(novaPosFoca, partida.getTablero().getTamanyo() - 1);
		novaPosFoca = Math.max(0, novaPosFoca);
		foca.setPos(novaPosFoca);

		// Sincronitzar posició visual
		int indiceFichaFoca = fichas.length - 1;
		posiciones[indiceFichaFoca] = novaPosFoca;
		int[] np = obtenerFilaColumna(novaPosFoca);
		GridPane.setRowIndex(fichas[indiceFichaFoca], np[0]);
		GridPane.setColumnIndex(fichas[indiceFichaFoca], np[1]);

		StringBuilder msg = new StringBuilder("🦭 La Foca es mou a " + novaPosFoca + ".");

		// Comprovar interaccions amb cada pingüí
		for (int i = 0; i < partida.getJugadores().size(); i++) {
			Jugador j = partida.getJugadores().get(i);
			if (!(j instanceof Pinguino)) continue;
			Pinguino pingu = (Pinguino) j;
			int posPingu = pingu.getPos();

			// Passa per sobre → envia al forat anterior
			if (posPingu > posAntesFoca && posPingu < novaPosFoca) {
				foca.golpearJugador(partida, pingu);
				int posNova = Math.max(0, pingu.getPos());
				pingu.setPos(posNova);
				posiciones[i] = posNova;
				int[] np2 = obtenerFilaColumna(posNova);
				GridPane.setRowIndex(fichas[i], np2[0]);
				GridPane.setColumnIndex(fichas[i], np2[1]);
				msg.append(" Ha colpejat " + pingu.getNom() + " de passada!");
			}
			// Coincideix exactament → li roba la meitat de l'inventari
			else if (posPingu == novaPosFoca) {
				foca.aplastarJugador(pingu);
				msg.append(" Ha aixafat " + pingu.getNom() + " i li ha robat la meitat de l'inventari!");
			}
		}
		eventos.setText(msg.toString());
	}

	// -------------------------------------------------------
	// AVANÇAR AL PRÒXIM TORN (incl. saltar torns perduts i torn foca)
	// -------------------------------------------------------

	private void avanzarAlSiguienteTurno() {
		Partida partida = gestorPartida.getPartida();
		if (partida.isFinalizada()) return;

		partida.siguienteTurno();

		// Si és el torn de la foca, l'executem automàticament i tornem a avançar
		if (modoImposible && partida.getJugadorActual() == indiceFoca) {
			ejecutarTurnoFoca();
			partida.siguienteTurno();
		}

		// Si el pròxim pingüí ha perdut el torn, el saltem
		Jugador seg = partida.getJugadores().get(partida.getJugadorActual());
		if (seg instanceof Pinguino) {
			Pinguino segP = (Pinguino) seg;
			if (!segP.isJuega()) {
				eventos.setText(segP.getNom() + " perd el torn.");
				segP.setJuega(true);
				avanzarAlSiguienteTurno();
				return;
			}
		}

		actualizarInventarioUI();
		marcarJugadorActual();
		dadoResultText.setText("Torn de: " + partida.getJugadores().get(partida.getJugadorActual()).getNom());
	}

	// -------------------------------------------------------
	// LÒGICA COMUNA: tirar un dau concret
	// -------------------------------------------------------

	private void tirarDadoConcreto(Dado d) {
		Partida partida = gestorPartida.getPartida();
		if (partida.isFinalizada()) return;

		int indice = partida.getJugadorActual();
		if (indice == indiceFoca) return;

		Jugador j = partida.getJugadores().get(indice);
		if (!(j instanceof Pinguino)) return;
		Pinguino pingu = (Pinguino) j;

		int resultado = d.tirar();
		dadoResultText.setText("Ha sortit: " + resultado);
		eventos.setText(pingu.getNom() + " tira " + d.getNom() + " → " + resultado);

		int novaPos = Math.max(0, Math.min(pingu.getPos() + resultado, partida.getTablero().getTamanyo() - 1));
		pingu.setPos(novaPos);

		// Desactivar botons durant l'animació
		dado.setDisable(true);
		rapido.setDisable(true);
		lento.setDisable(true);
		peces.setDisable(true);
		nieve.setDisable(true);

		Node ficha = fichas[indice];
		int oldPosition = posiciones[indice];
		posiciones[indice] = novaPos;

		int[] oldPos2 = obtenerFilaColumna(oldPosition);
		int[] newPos2 = obtenerFilaColumna(novaPos);
		double cellWidth  = tablero.getWidth()  / this.columnas;
		double cellHeight = tablero.getHeight() / this.filas;
		double dx = (newPos2[1] - oldPos2[1]) * cellWidth;
		double dy = (newPos2[0] - oldPos2[0]) * cellHeight;

		TranslateTransition slide = new TranslateTransition(Duration.millis(350), ficha);
		slide.setByX(dx);
		slide.setByY(dy);
		final int indiceFichaFinal = indice;
		slide.setOnFinished(e -> {
			ficha.setTranslateX(0);
			ficha.setTranslateY(0);
			GridPane.setRowIndex(ficha, newPos2[0]);
			GridPane.setColumnIndex(ficha, newPos2[1]);

			javafx.application.Platform.runLater(() ->
				postMovimientoPinguino(pingu, indiceFichaFinal)
			);
		});
		slide.play();
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
	private void handleRapido() {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		Pinguino pingu = (Pinguino) partida.getJugadores().get(indice);
		if (pingu.getInv().contarItem(new Dado("Rapido", 0)) > 0) {
			pingu.quitarItem(new Dado("Rapido", 0));
			tirarDadoConcreto(new Dado("Rapido", 1));
		}
	}

	@FXML
	private void handleLento() {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		Pinguino pingu = (Pinguino) partida.getJugadores().get(indice);
		if (pingu.getInv().contarItem(new Dado("Lento", 0)) > 0) {
			pingu.quitarItem(new Dado("Lento", 0));
			tirarDadoConcreto(new Dado("Lento", 1));
		}
	}

	/**
	 * Usar un peix per subornar la foca: la bloqueja 2 torns.
	 */
	@FXML
	private void handlePeces() {
		Partida partida = gestorPartida.getPartida();
		int indice = partida.getJugadorActual();
		Pinguino pingu = (Pinguino) partida.getJugadores().get(indice);

		if (pingu.getInv().contarItem(new Pez(0)) <= 0) {
			eventos.setText("No tens peixos!");
			return;
		}

		if (!modoImposible || indiceFoca < 0) {
			eventos.setText("No hi ha Foca en aquesta partida.");
			return;
		}

		Foca foca = (Foca) partida.getJugadores().get(indiceFoca);
		pingu.usarItem(new Pez(0));
		pingu.quitarItem(new Pez(0));
		foca.esSobornado(partida, pingu);
		eventos.setText(pingu.getNom() + " ha subornat la Foca amb un peix! Bloquejada 2 torns.");
		actualizarInventarioUI();
	}

	/**
	 * El botó de neu és informatiu — les boles s'utilitzen en batalla.
	 */
	@FXML
	private void handleNieve() {
		eventos.setText("❄ Les boles de neu s'utilitzen en batalla! "
			+ "Coincideix amb un altre pingüí per activar la guerra de neu.");
	}

	// -------------------------------------------------------
	// HANDLERS DEL MENÚ
	// -------------------------------------------------------

	@FXML private void handleNewGame() {
		try {
			Stage stage = (Stage) tablero.getScene().getWindow();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PantallaMenu.fxml"));
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			stage.setTitle("El Juego del Pingüino");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML private void handleSaveGame() {
		eventos.setText("💾 Partida guardada! (Funcionalitat de BBDD pendent de connexió)");
	}

	@FXML private void handleLoadGame() {
		eventos.setText("📂 Cargar partida (Funcionalitat de BBDD pendent de connexió)");
	}

	@FXML private void handleQuitGame() { System.exit(0); }

	public void setGestorPartida(GestorPartida gestorPartida) {
		this.gestorPartida = gestorPartida;
	}
}
