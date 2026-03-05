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
			int i = 0;
			for(i = pingu.getPos()+1; i < tablero.size(); i++) {
				if(tablero.get(i) instanceof Trineo) {
					pingu.setPos(i);
					i = tablero.size() + 1;
				}else {
					pingu.setPos(pos+((p.getTablero().getCasillas().size())/10));
				}
			}
		}
	}
}
