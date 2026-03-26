package MODELOS;

public class Agujero extends Casilla {

	public Agujero(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			if(!pingu.isJuega()) {
				int posActual = pingu.getPos();
				int posAgujeroAnterior = p.getTablero().agujeroAnterior(posActual);
				int nuevaPos;
				if(posAgujeroAnterior >= 0) {
					nuevaPos = posAgujeroAnterior;
				}else {
					nuevaPos = 0;
				}
				pingu.setPos(nuevaPos);
			}
		}
	}
}
