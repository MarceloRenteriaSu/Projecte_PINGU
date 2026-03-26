package MODELOS;
import java.util.ArrayList;

public class Partida {
	
	//ATRIBUTOS
	private Tablero tablero;
	private ArrayList<Jugador>jugadores;
	private int turnos;
	private int jugadorActual;
	private boolean finalizada;
	private Jugador ganador;
	
	//CONSTRUCTOR
	public Partida(Tablero tablero, ArrayList<Jugador> jugadores) {
		this.tablero = tablero;
		this.jugadores = new ArrayList<>(jugadores);
		this.turnos = 0;
		this.jugadorActual = 0;
		this.finalizada = false;
		this.ganador = null;
	}
	
	//GETTERS Y SETTERS
	public Tablero getTablero() {
		return tablero;
	}

	public void setTablero(Tablero tablero) {
		this.tablero = tablero;
	}

	public ArrayList<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(ArrayList<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public int getTurnos() {
		return turnos;
	}

	public void setTurnos(int turnos) {
		this.turnos = turnos;
	}

	public int getJugadorActual() {
		return jugadorActual;
	}

	public void setJugadorActual(int jugadorActual) {
		this.jugadorActual = jugadorActual;
	}

	public boolean isFinalizada() {
		return finalizada;
	}

	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	public Jugador getGanador() {
		return ganador;
	}

	public void setGanador(Jugador ganador) {
		this.ganador = ganador;
	}
	
	public void siguienteTurno() {
		jugadorActual = (jugadorActual + 1) % jugadores.size();
		turnos++;
	}
	
}
