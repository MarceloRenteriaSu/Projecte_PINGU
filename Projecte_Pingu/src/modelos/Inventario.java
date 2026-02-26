package modelos;

import java.util.ArrayList;

public class Inventario {
	//ATRIBUTOS
	protected ArrayList<item> inv;
	
	//CONSTRUCTOR
	public Inventario(ArrayList<item> inv) {
		this.inv = inv;
	}
	
	//GETTER Y SETTER
	public ArrayList<item> getInv() {
		return inv;
	}

	public void setInv(ArrayList<item> inv) {
		this.inv = inv;
	}
	

	public int contarItem(String nom) {
		int cantidad = 0;
		for(item it : inv) {
			if(it.getNombre().equalsIgnoreCase(nom)) {
				cantidad += it.getCantidad();
			}
		}
		return cantidad;

	public int totalItems() {
		int total = 0;
		for(item it : inv) {
			total = total + it.getCantidad();
		}
		return total;

	}
}
