package GESTORES;
import MODELOS.*;

public class GestorJugador {

    public void jugadorSeMueve(Jugador j, int pasos, Tablero t) {
    	int nuevaPos = j.getPos()+pasos;
    	
    	if(nuevaPos > t.getTamanyo()-1) {
    		nuevaPos = t.getTamanyo() -1;
    	}
    	
    	if (nuevaPos < 0 ) {
    		nuevaPos = 0;
    	}
    	
    	j.setPos(nuevaPos);
    }

    public void jugadorFinalizaTurno(Jugador j) {
    	if (j instanceof Pinguino) {
            Pinguino p = (Pinguino) j;
            p.setJuega(false);
        }
    }

    public void piguinoEvento(Pinguino p, Partida partida, Casilla c) {
    	if (p != null && partida != null && c instanceof Evento) {
            c.realizarAccion(partida, p);
        }
    }

    public void pinguinoGuerra(Pinguino p1, Pinguino p2) {
    	if (p1 != null && p2 != null) {
    		p1.GestionarBatalla(p2);
    	}
    }

    public void focaInteractua(Pinguino p, Foca f, Partida partida) {
    	if (p != null && f != null && partida != null) {
    		 f.golpearJugador(partida, p);
    	}
    }
    
    public void jugadorUsaItem(Jugador j, Item i) {
        if (j instanceof Pinguino) {
            Pinguino p = (Pinguino) j;
            p.usarItem(i);
        }
    }
}