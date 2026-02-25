package modelos;

import java.util.ArrayList;

public class Partida {
	
	//ATRIBUTOS
	protected Tablero tablero;
	protected ArrayList<jugador> jugador;
	protected int turnos;
	protected int jugadorActual;
	protected boolean fin;
	protected jugador ganador;
	
	//CONSTRUCTOR
	public Partida(Tablero tablero, ArrayList<jugador> jugador, int turnos, int jugadorActual, boolean fin, jugador ganador) {
		this.tablero = tablero;
		this.jugador = jugador;
		this.turnos = turnos;
		this.jugadorActual = jugadorActual;
		this.fin = fin;
		this.ganador = ganador;
	}
	
	//GETTERS Y SETTERS
	public Tablero getTablero() {
		return tablero;
	}
	public void setTablero(Tablero tablero) {
		this.tablero = tablero;
	}
	public ArrayList<jugador> getJugador() {
		return jugador;
	}
	public void setJugador(ArrayList<jugador> jugador) {
		this.jugador = jugador;
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
	public boolean isFin() {
		return fin;
	}
	public void setFin(boolean fin) {
		this.fin = fin;
	}
	public jugador getGanador() {
		return ganador;
	}
	public void setGanador(jugador ganador) {
		this.ganador = ganador;
	}
	
	//MÉTODOS
	public jugador jugadorActual() {
		return jugador.get(jugadorActual);
	}
	

	

}
