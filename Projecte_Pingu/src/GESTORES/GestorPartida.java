package GESTORES;
import java.util.ArrayList;
import java.util.Random;

import MODELOS.Dado;
import MODELOS.Partida;
import MODELOS.Tablero;
import MODELOS.Jugador;

public class GestorPartida {
	protected Partida partida;
	protected GestorTablero gestorTablero;
	protected GestorJugador gestorJugador;
	protected GestorBBDD gestorBBDD;
	protected Random rd;
	
	public void nuevaPartida(ArrayList<Jugador>jugador, Tablero tablero) {
		
	}
	
	public int tirarDado(Jugador j, Dado dado) {
		int num = 0;
		return num;
	}
	
	public void ejecutarTuroCompleto() {
		
	}
	
	public void procesarTurnoJugador(Jugador j) {
		
	}
	
	public void actualizarEstadoTablero() {
		
	}
	
	public void siguienteTurno() {
		
	}
	
	public Partida getPartida() {
		return new Partida(null, null);
	}
	
	public void guardarPartida() {
		
	}
	
	public void cargarPartida(int id) {
		
	}
	
	
	
	

}
