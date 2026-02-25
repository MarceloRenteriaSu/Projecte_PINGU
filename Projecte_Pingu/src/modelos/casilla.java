package modelos;

public abstract class casilla {
	//ATRIBUTOS
	protected int pos;
	
	//Constructor
	public casilla(int pos) {
		this.pos = pos;
	}

	//GETTTER Y SETTER
	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
	//MÉTODO REALIZARACCION()
	public void realizarAccion(Partida partida, jugador jugador) {
		
	}
	
}
