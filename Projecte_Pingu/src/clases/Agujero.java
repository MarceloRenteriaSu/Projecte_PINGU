package clases;

import java.util.ArrayList;

public class Agujero extends Casilla {

	public Agujero(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		ArrayList<Casilla>tablero = p.getTablero().getCasillas();	
		if(j instanceof Pinguino) {
			boolean encontrado = false;
			Pinguino pingu =(Pinguino) j;
			int i = 0;
			for(i = pingu.getPos()-1; i > 0; i--) {
				if(tablero.get(i) instanceof Agujero) {
					pingu.setPos(i);
					encontrado = true;
					i = 0;
				}
			}
			if(encontrado == false) {
				pingu.setPos(0);
			}
		}
	}
}
