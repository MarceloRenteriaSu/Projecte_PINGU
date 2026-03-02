package clases;
import java.util.ArrayList;

public class Trineo extends Casilla {

	public Trineo(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		ArrayList<Casilla>tablero = p.getTablero().getCasillas();
		if(j instanceof Pinguino) {
			Pinguino pingu =(Pinguino) j;
			for(int i = pingu.getPos(); i < tablero.size(); i++) {
				if(tablero.get(i) instanceof Trineo) {
					pingu.setPos(i);
					i = tablero.size() + 1;
				}
			}
		}
	}
}
