package controlador;
import java.util.ArrayList;
import java.util.Random;

import modelos.Dado;
import modelos.Partida;
import modelos.Tablero;
import modelos.jugador;

public class GestorPartida {
	protected Partida partida;
	protected GestorTablero gestorTablero;
	protected GestorJugador gestorJugador;
	protected GestorBBDD gestorBBDD;
	protected Random rd;
	
	public void nuevaPartida(ArrayList<jugador>jugador, Tablero tablero) {
		
	}
	
	public int tirarDado(jugador j, Dado dado) {
		int num = 0;
		return num;
	}
	
	public void ejecutarTuroCompleto() {
		
	}
	
	public void procesarTurnoJugador(jugador j) {
		
	}
	
	public void actualizarEstadoTablero() {
		
	}
	
	public void siguienteTurno() {
		
	}
	
	public Partida getPartida() {
		Partida p = new Partida(null, null, 0, 0);
		return p;
	}
	
	public void guardarPartida() {
		
	}
	
	public void cargarPartida(int id) {
		
	}
	
	
	
	

}
