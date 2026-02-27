package modelos;

public class Oso extends casilla {
	//CONSTRUCTOR
	public Oso(int pos) {
		super(pos);
	}
	
	@Override
	public void realizarAccion(Partida partida, Pinguino p) {
		if(p != null) {
			if(p.getInv().contarItem("Pez") == 0) {
				p.setPos(0);
			}else {
				p.usarItem(new Pez(0));
			}
		}
	}
}
