package clases;
import java.util.ArrayList;

public class Partida {
	
	//ATRIBUTOS
	protected Tablero tablero;
	protected ArrayList<Jugador>jugadores;
	protected int turnos;
	protected int jugadorActual;
	protected boolean finalizada;
	protected Jugador ganador;
	
	//CONSTRUCTOR
	public Partida(Tablero tablero, ArrayList<Jugador> jugadores, int turnos, int jugadorActual, boolean finalizada,
			Jugador ganador) {
		this.tablero = tablero;
		this.jugadores = jugadores;
		this.turnos = turnos;
		this.jugadorActual = jugadorActual;
		this.finalizada = finalizada;
		this.ganador = ganador;
	}
	
	//CONSTRUCTOR
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
	
}
