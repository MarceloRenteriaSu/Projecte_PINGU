package clases;

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
		if(p != null && !p.isFinalizada()) {
			int Jactual = p.getJugadorActual();
			if(Jactual >= 0 || Jactual < p.getJugadores().size()) {
				Pinguino pingu = (Pinguino) p.getJugadores().get(Jactual);
				boolean turnoTerminado = true;
				if(!pingu.isJuega()) {
					turnoTerminado = true;
					pingu.setJuega(true);
				}
				if(turnoTerminado) {
					p.siguienteTurno();
					comprobarGanador(p);
				}
			}
		}
	}
	
	//MÉTODO COMPROBAR GANADOR
	private void comprobarGanador(Partida partida) {
		int ultimaCasilla = partida.getTablero().getCasillas().size() -1;
		Pinguino ganador = null;
		
		for(Jugador j : partida.getJugadores()) {
			Pinguino p = (Pinguino) j;
			if(p.getPos() >= ultimaCasilla) {
				if(ganador == null) {
					ganador = p;
					partida.setGanador(p);
					partida.setFinalizada(true);
				}	
			}
		}
	}
	
}
