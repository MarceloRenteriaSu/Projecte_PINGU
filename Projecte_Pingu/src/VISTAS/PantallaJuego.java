package VISTAS;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import GESTORES.GestorPartida;
import MODELOS.*;

public class PantallaJuego {

	// Menu items
	@FXML
	private MenuItem newGame;
	@FXML
	private MenuItem saveGame;
	@FXML
	private MenuItem loadGame;
	@FXML
	private MenuItem quitGame;

	// Buttons
	@FXML
	private Button dado;
	@FXML
	private Button rapido;
	@FXML
	private Button lento;
	@FXML
	private Button peces;
	@FXML
	private Button nieve;

	// Texts
	@FXML
	private Text dadoResultText;
	@FXML
	private Text rapido_t;
	@FXML
	private Text lento_t;
	@FXML
	private Text peces_t;
	@FXML
	private Text nieve_t;
	@FXML
	private Text eventos;

	// Game board and player pieces
	@FXML
	private GridPane tablero;
	@FXML
	private Circle P1;
	@FXML
	private Circle P2;
	@FXML
	private Circle P3;
	@FXML
	private Circle P4;

	private GestorPartida gestorPartida;
	// ONLY FOR TESTING!!!
	private int p1Position = 0; // Tracks current position (from 0 to 49 in a 5x10 grid)
	private static final int COLUMNS = 5;

	private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";
	private final Random rand = new Random();
	
	private int[] posiciones = {0,0,0,0};
	private Node[] fichas;

	@FXML
	private void initialize() {
		eventos.setText("¡El juego ha comenzado!");
		Tablero t = new Tablero(50);
		gestorPartida = new GestorPartida();
		
		ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
		ArrayList<Item> inv = new ArrayList<Item>();
		Inventario inventario = new Inventario(inv);
		ArrayList<Item> inv2 = new ArrayList<Item>();
		Inventario inventario2 = new Inventario(inv2);
		ArrayList<Item> inv3 = new ArrayList<Item>();
		Inventario inventario3 = new Inventario(inv3);
		ArrayList<Item> inv4 = new ArrayList<Item>();
		Inventario inventario4 = new Inventario(inv4);
		Dado dado = new Dado("Normal", 1);
		inventario.getInv().add(dado);
		inventario2.getInv().add(dado);
		inventario3.getInv().add(dado);
		inventario4.getInv().add(dado);
		
		jugadores.add(new Pinguino("Jugador1",0 , "Azul",inventario));
		jugadores.add(new Pinguino("Jugador2",0 , "Rojo",inventario2));
		jugadores.add(new Pinguino("Jugador3",0 , "Amarillo",inventario3));
		jugadores.add(new Pinguino("Jugador4",0 , "Verde",inventario4));

		gestorPartida.nuevaPartida(t, jugadores);
		
		fichas = new Node[]{P1, P2, P3, P4};

		// Show board info
		mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
	}
	
	
	private int[] obtenerFilaColumna(int posicion) {

	    int row = posicion / COLUMNS;
	    int col;

	    if (row % 2 == 0) {
	        col = posicion % COLUMNS;
	    } else {
	        col = COLUMNS - 1 - (posicion % COLUMNS);
	    }

	    return new int[]{row, col};
	}

	private void mostrarTiposDeCasillasEnTablero(Tablero t) {

		tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));

	    int totalCasillas = t.getTamanyo();

	    for (int i = 0; i < totalCasillas; i++) {

	        Casilla casilla = t.getCasillas().get(i);

	        if (i > 0 && i < totalCasillas - 1) {

	            String tipo = casilla.getClass().getSimpleName();

	            Text texto = new Text(tipo);
	            texto.setUserData(TAG_CASILLA_TEXT);
	            texto.getStyleClass().add("cell-type");

	            int[] pos = obtenerFilaColumna(i);

	            GridPane.setRowIndex(texto, pos[0]);
	            GridPane.setColumnIndex(texto, pos[1]);

	            tablero.getChildren().add(texto);
	        }
	    }
	}

	// Menu actions
	@FXML
	private void handleNewGame() {
		System.out.println("New game.");
		// TODO
	}

	@FXML
	private void handleSaveGame() {
		System.out.println("Saved game.");
		// TODO
	}

	@FXML
	private void handleLoadGame() {
		System.out.println("Loaded game.");
		// TODO
	}

	@FXML
	private void handleQuitGame() {
		System.out.println("Exit...");
		// TODO
	}

	// Button actions
	@FXML
	private void handleDado(ActionEvent event) {

	    Partida partida = gestorPartida.getPartida();

	    int jugador = partida.getJugadorActual();

	    Pinguino pingu = (Pinguino) partida.getJugadores().get(jugador);

	    Dado d = (Dado) pingu.getInv().getInv().get(0);

	    int resultado = gestorPartida.tirarDado(pingu, d);
	    dadoResultText.setText("Ha salido: " + resultado);
	    moverJugador(jugador, resultado);

	    partida.siguienteTurno();
	}

	
	private void moverJugador(int jugador, int steps) {

	    dado.setDisable(true);

	    Node ficha = fichas[jugador];

	    int oldPosition = posiciones[jugador];

	    posiciones[jugador] += steps;

	    if (posiciones[jugador] >= gestorPartida.getPartida().getTablero().getTamanyo() -1) {
	        posiciones[jugador] = gestorPartida.getPartida().getTablero().getTamanyo() -1;
	    }

	    if (posiciones[jugador] < 0) {
	        posiciones[jugador] = 0;
	    }

	    int newPosition = posiciones[jugador];

	    int[] oldPos = obtenerFilaColumna(oldPosition);
	    int[] newPos = obtenerFilaColumna(newPosition);

	    int oldRow = oldPos[0];
	    int oldCol = oldPos[1];

	    int newRow = newPos[0];
	    int newCol = newPos[1];

	    double cellWidth = tablero.getWidth() / COLUMNS;
	    double cellHeight = tablero.getHeight() / 10;

	    double dx = (newCol - oldCol) * cellWidth;
	    double dy = (newRow - oldRow) * cellHeight;

	    TranslateTransition slide = new TranslateTransition(Duration.millis(350), ficha);

	    slide.setByX(dx);
	    slide.setByY(dy);

	    slide.setOnFinished(e -> {

	        ficha.setTranslateX(0);
	        ficha.setTranslateY(0);

	        GridPane.setRowIndex(ficha, newRow);
	        GridPane.setColumnIndex(ficha, newCol);

	        dado.setDisable(false);
	    });

	    slide.play();
	}

	@FXML
	private void handleRapido() {
		System.out.println("Fast.");
		// TODO
	}

	@FXML
	private void handleLento() {
		System.out.println("Slow.");
		// TODO
	}

	@FXML
	private void handlePeces() {
		System.out.println("Fish.");
		// TODO
	}

	@FXML
	private void handleNieve() {
		System.out.println("Snow.");
		// TODO
	}

	public void setGestorPartida(GestorPartida gestorPartida) {
		this.gestorPartida = gestorPartida;
	}
}
