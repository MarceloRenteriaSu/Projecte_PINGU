package clases;
import java.util.ArrayList;
public class Inventario {
	protected ArrayList<Item> inv;
	public Inventario(ArrayList<item> inv) {
		this.inv = inv;
	}
	public ArrayList<Item> getInv() {
		return inv;
	}
	public void setInv(ArrayList<Item> inv) {
		this.inv = inv;
	}
	
	public int contarItem(Item i) {
		int cantidad = 0;
		for(Item it : inv) {
			if(it.getNombre().equals(i.getNombre())) {
				cantidad += it.getCantidad();
			}
		}
		return cantidad;
	}
	
	public int totalItems() {
		int total = 0;
		for(item it : inv) {
			total = total + it.getCantidad();
		}
		return total;

	}
	
	public int contarDados() {
		int total = 0;
		for(item it : inv) {
			if(it instanceof Dado) {
				total += 1;
			}
		}
		return total;
	}
}
