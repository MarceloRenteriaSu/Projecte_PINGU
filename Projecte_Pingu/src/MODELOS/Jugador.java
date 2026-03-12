package MODELOS;

public abstract class Jugador {
	private int pos;
	private String nom;
	
	public Jugador(String nom, int pos) {
		this.nom = nom;
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void moverPosicio(int p) {
		
	}
}
