package modelos;

public class SueloQuebradizo extends casilla {
	//CONSTRUCTOR
	public SueloQuebradizo(int pos) {
		super(pos);
	}
	
	@Override
	public void realizarAccion(Partida partida, Pinguino p) {
		if(p != null) {
			if(p.getInv().totalItems() == 0) {
				//nada
			}else if(p.getInv().totalItems() <= 5) {
				//pierde turno
			}else if(p.getInv().totalItems() > 5) {
				p.setPos(0);
			}
		}
	}
}
