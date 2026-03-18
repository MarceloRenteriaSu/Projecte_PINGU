package GESTORES;
import MODELOS.*;

public class GestorTablero {
	//MÉTODO EJECUTARCASILLA
	public void ejecutarCasilla(Partida partida, Pinguino p, Casilla c) {
		if(partida != null && p != null && c != null) {
			int posicionActual = p.getPos();
			if(c.getPos() == posicionActual) {
				c.realizarAccion(partida, p);
			}
			if(p.getPos() >= partida.getTablero().getCasillas().size() -1) {
				partida.setFinalizada(true);
				partida.setGanador(p);
			}
		}
	}
	
	//MÉTODO COMPROBAR FIN DE TURNO
	public void comprobarFinTurno(Partida p) {
		if (p != null && !p.isFinalizada()) {
	        int indiceActual = p.getJugadorActual();
	        if (indiceActual >= 0 && indiceActual < p.getJugadores().size()) {
	        	Jugador jugadorActual = p.getJugadores().get(indiceActual);
		        if (jugadorActual instanceof Pinguino) {
		            Pinguino pingu = (Pinguino) jugadorActual;
		            if (!pingu.isJuega()) {
		                pingu.setJuega(true);
		            }
		            p.siguienteTurno();
		        } else if (jugadorActual instanceof Foca) {
		            p.siguienteTurno();
		        }
		        //comprobarGanador(p);
	        }
		}
	}
	
	/*//MÉTODO COMPROBAR GANADOR
	private void comprobarGanador(Partida partida) {
		if (partida != null && !partida.isFinalizada()) {
			int ultimaCasilla = partida.getTablero().getCasillas().size() - 1;
	        for (Jugador j : partida.getJugadores()) {
	            // Només els pingüins poden guanyar la partida
	            if (j instanceof Pinguino) {
	                Pinguino p = (Pinguino) j;
	                if (p.getPos() >= ultimaCasilla) {
	                    partida.setGanador(p);
	                    partida.setFinalizada(true);
	                    System.out.println("¡Guanya " + p.getNom() + "!");
	                    return;
	                }
	            }
		}
        
        }
	}*/
	
}
