package MODELOS;

public class Trineo extends Casilla {

	public Trineo(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			int posActual = pingu.getPos();
			int tamanyo = p.getTablero().getTamanyo();
			int posTrineoProximo = p.getTablero().trineoPosterior(posActual);
			int nuevaPos;
			if(posTrineoProximo != -1) {
				nuevaPos = posTrineoProximo;
			} else {
				int avanzar = tamanyo / 10;
				nuevaPos = avanzar;
				if(nuevaPos >= tamanyo) {
					nuevaPos = tamanyo - 1;
				}
			}
			pingu.setPos(nuevaPos);
		}
	}
}
