package modelos;

public class Foca extends jugador {
	//ATRIBUTOS
	protected boolean soborno;
	
	//CONSTRUCTOR
	public Foca(String nom, int pos) {
		super(nom, pos);
		this.soborno = false;
	}
	
	//GETTERS Y SETTERS
	public boolean isSoborno() {
		return soborno;
	}

	public void setSoborno(boolean soborno) {
		this.soborno = soborno;
	}

	//MÉTODO APLASTARJUGADOR()
	public void aplastarJugador(Pinguino p) {
		
	}
	
	//MÉTODO GOLPEARJUGADOR()
	public void golpearJugador(Pinguino p) {
		
	}
	
	//MÉTODO ESSOBORNADO()
	public void esSobornado() {
		
	}
}
