package modelos;

public abstract class jugador {
	//ATRIBUTOS
	protected String nom;
	protected String color;
	protected int pos;
	
	//CONSTRUCTOR 
	public jugador(String nom, int pos) {
		this.nom = nom;
		this.pos = pos;
	}
	
	//GETTERS Y SETTERS
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
	//MÉTODO MOVER POS
	public void moverPos(int p) {
		
	}
	
	
}
