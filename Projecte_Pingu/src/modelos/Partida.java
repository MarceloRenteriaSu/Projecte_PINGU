package modelos;

import java.util.ArrayList;
import java.util.Random;

public class Partida {
	
	//ATRIBUTOS
	protected Tablero tablero;
	protected ArrayList<jugador> jugador;
	protected int turnos;
	protected int jugadorActual;
	protected boolean fin;
	protected jugador ganador;
	
	//CONSTRUCTOR
	public Partida(Tablero tablero, ArrayList<jugador> jugador, int turnos, int jugadorActual) {
		this.tablero = tablero;
		this.jugador = jugador;
		this.turnos = turnos;
		this.jugadorActual = jugadorActual;
		this.fin = false;
		this.ganador = null;
	}
	
	//GETTERS Y SETTERS
	public Tablero getTablero() {
		return tablero;
	}
	public void setTablero(int cantidad) {
		ArrayList<casilla> lista = new ArrayList<>();
		if(cantidad < 50) {
        	System.out.println("ERROR");
        	cantidad = 50;
        }
            Random rand = new Random();
            lista.add(new Normal(0));
            for (int i = 1; i < cantidad; i++) {
                int r = rand.nextInt(100);
                if (r < 55) {
                    lista.add(new Normal(i));
                } else if (r < 65) {
                    lista.add(new Oso(i));
                } else if (r < 75) {
                    lista.add(new Agujero(i));
                } else if (r < 88) {
                    lista.add(new Trineo(i));
                } else if (r < 95) {
                    lista.add(new Evento(i));
                } else {
                    lista.add(new SueloQuebradizo(i));
                }
            }
        this.tablero = new Tablero(lista);
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
