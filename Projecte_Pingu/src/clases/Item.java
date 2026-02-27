package clases;

public abstract class Item {
	protected String Nom;
	protected int Cantidad;

	public Item(int Cantidad) {

		this.Nom = null;
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
