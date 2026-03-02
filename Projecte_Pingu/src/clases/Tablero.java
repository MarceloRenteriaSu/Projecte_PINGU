package clases;
import java.util.ArrayList;

public class Tablero {
	//ATRIBUTOS
	protected ArrayList<Casilla>casillas;
	
	//CONSTRUCTOR
	public Tablero(ArrayList<Casilla> casillas) {
		this.casillas = casillas;
	}
	
	//GETTERS Y SETTERS
	public ArrayList<Casilla> getCasillas() {
		return casillas;
	}

	public void setCasillas(ArrayList<Casilla> casillas) {
		this.casillas = casillas;
	}
	
	public Casilla getCasilla(int pos) {
		if(pos >= 0 && pos < casillas.size()) {
			return casillas.get(pos);
		}
		return null;
	}
	
	private Casilla crearCasilla(String tipo, int pos) {
		switch(tipo) {
		case "Oso":
			
		}
	}
	
	public void actualizarTablero() {
		
	}
	
}
