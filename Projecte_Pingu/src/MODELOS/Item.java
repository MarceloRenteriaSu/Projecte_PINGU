package MODELOS;

public abstract class Item {
	private String Nom;
	private int Cantidad;

	public Item(int Cantidad) {
		this.Nom = null;
		this.Cantidad = Cantidad;
	}
	
	public Item(String nombre, int Cantidad) {
		this.Nom = nombre;
		this.Cantidad = Cantidad;
	}

	public String getNom() {
		return Nom;
	}

	public void setNom(String nom) {
		Nom = nom;
	}

	public int getCantidad() {
		return Cantidad;
	}

	public void setCantidad(int cantidad) {
		Cantidad = cantidad;
	}
	

}
