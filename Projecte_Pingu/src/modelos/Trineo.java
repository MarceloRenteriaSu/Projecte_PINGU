package modelos;

import java.util.ArrayList;

public class Trineo extends casilla {
	//CONSTRUCTOR
	public Trineo(int pos) {
		super(pos);
	}
	
	@Override
	public void realizarAccion(Partida partida, Pinguino p) {
		if(partida != null && p != null) {
			ArrayList<casilla> tablero = partida.getTablero().getCasilla();
			int actual = p.getPos();
			for(int i = actual; i < tablero.size(); i++) {
				if(tablero.get(i) instanceof Trineo) {
					p.setPos(i);
					i = tablero.size() + 1;
				}
			}
		}
	}
}
