package MODELOS;

public class Agujero extends Casilla {

	public Agujero(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			int posActual = pingu.getPos();
			int posAgujeroAnterior = p.getTablero().agujeroAnterior(posActual);
			int nuevaPos = (posAgujeroAnterior >= 0) ? posAgujeroAnterior : 0;
			pingu.setPos(nuevaPos);
		}
	}
}
