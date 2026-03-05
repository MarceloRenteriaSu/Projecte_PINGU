package clases;

public class Oso extends Casilla {

	public Oso(int pos) {
		super(pos);
	}

	@Override
	public void realizarAccion(Partida p, Jugador j) {
		Pinguino pingu = (Pinguino)j;
		Inventario inv = pingu.getInv();
		if(inv.contarItem(new Pez(0)) > 0) {
			pingu.usarItem(new Pez(0));
		}else {
			pingu.setPos(0);
		}
		
	}
	
	

}
