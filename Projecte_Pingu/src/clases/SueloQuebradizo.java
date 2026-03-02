package clases;

public class SueloQuebradizo extends Casilla {

	public SueloQuebradizo(int pos) {
		super(pos);
	}
	
	

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		if(j instanceof Pinguino) {
			Pinguino pingu =(Pinguino)j;
			if(pingu.getInv().totalItems() > 5) {
				pingu.setPos(0);
			}else if(pingu.getInv().totalItems() <= 5 && pingu.getInv().totalItems() != 0) {
				pingu.perderTurno();
			}
		}
		
	}

	
}
