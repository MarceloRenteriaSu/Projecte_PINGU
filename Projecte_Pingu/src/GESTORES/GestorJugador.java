package GESTORES;
import MODELOS.*;

public class GestorJugador {

    public void jugadorUsaItem(String nombreItem) {
        // TODO: implementar uso de item por el jugador
    }

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
        // TODO: implementar fin de turno del jugador
    }

    public void piguinoEvento(Pinguino p) {
        // TODO: implementar evento del pingüino
    }

    public void pingüinoGuerra(Pinguino p1, Pinguino p2) {
        // TODO: implementar guerra entre pingüinos
    }

    public void focaInteractua(Pinguino p, Foca f) {
        // TODO: implementar interacción con la foca
    }
}