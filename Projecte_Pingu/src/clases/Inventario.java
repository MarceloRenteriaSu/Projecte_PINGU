package clases;
import java.util.ArrayList;
public class Inventario {
	//ATRIBUTOS
	protected ArrayList<Item> inv;
	//CONSTRUCTOR
	public Inventario(ArrayList<Item> inv) {
		this.inv = inv;
	}
	//GETTERS Y SETTERS
	public ArrayList<Item> getInv() {
		return inv;
	}
	public void setInv(ArrayList<Item> inv) {
		this.inv = inv;
	}
	
	//MÉTODO PARA CONTAR LA CANTIDAD DE UN ITEM
	public int contarItem(Item item) {
		int cantidad = 0;
		for(Item it : inv) {
			if(it.getNom().equals(item.getNom())) {
				cantidad += it.getCantidad();
			}
		}
		return cantidad;
	}
	
	//MÉTODO PARA CONTAR LA CANTIDAD DE ITEMS EN EL INVENTARIO
	public int totalItems() {
		int total = 0;
		for(Item it : inv) {
			total = total + it.getCantidad();
		}
		return total;

	}
	
	//MÉTODO PARA CONTAR LOS DADOS
	public int contarDados() {
		int total = 0;
		for(Item it : inv) {
			if(it instanceof Dado) {
				total += 1;
			}
		}
		return total;
	}
}
