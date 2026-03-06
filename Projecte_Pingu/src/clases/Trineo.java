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
			boolean encontrado = false;
			for(i = pingu.getPos()+1; i < tablero.size(); i++) {
				if(tablero.get(i) instanceof Trineo) {
					encontrado = true;
					pingu.setPos(i);
					i = tablero.size()+1;
				}
			}
			if(encontrado == false) {
				pingu.setPos(pos+((p.getTablero().getCasillas().size())/10));
				
				if(pingu.getPos() >= p.getTablero().getCasillas().size()-1) {
					pingu.setPos(p.getTablero().getCasillas().size()-1);
				}
			}
		}
	}
}
