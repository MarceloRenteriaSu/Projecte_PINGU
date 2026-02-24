package modelos;

import java.util.ArrayList;

public class Inventario {
	//ATRIBUTOS
	protected ArrayList<item> inv;
	
	//CONSTRUCTOR
	public Inventario(ArrayList<item> inv) {
		this.inv = inv;
	}

	public ArrayList<item> getInv() {
		return inv;
	}

	public void setInv(ArrayList<item> inv) {
		this.inv = inv;
	}
	
}
