package MODELOS;

public class SueloQuebradizo extends Casilla {

	public SueloQuebradizo(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		if(j instanceof Pinguino) {
			Pinguino pingu =(Pinguino)j;
			int totalItems = pingu.getInv().totalItems();
			if(totalItems > 5) {
				pingu.setPos(0);
			}else if(totalItems <= 5 && totalItems != 0) {
				pingu.perderTurno();
				
			}
		}
		
	}

	
}
