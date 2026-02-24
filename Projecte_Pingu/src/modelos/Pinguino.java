package modelos;

public class Pinguino extends jugador {
	//ATRIBUTOS
	protected Inventario inv;
	
	public Pinguino(String nom, int pos, Inventario inv) {
		super(nom, pos);
		this.inv = inv;
		
	}

	public Inventario getInv() {
		return inv;
	}

	public void setInv(Inventario inv) {
		this.inv = inv;
	}
	
	public void batalla(Pinguino p) {
		
	}
	
	public void usarItem(item i) {
		
	}
	
	public void agregarItem(item i) {
		
	}
	
	public void quitarItem(item i) {
		
	}

}
