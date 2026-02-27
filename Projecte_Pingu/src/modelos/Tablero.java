package modelos;
import java.util.ArrayList;

public class Tablero {
	//ATRIBUTOS
	protected ArrayList<casilla> casilla;
	
	//CONSTRUCTOR
	public Tablero(ArrayList<casilla> casilla) {
		this.casilla = casilla;
	}
	
	//GETTERS Y SETTERS
	public ArrayList<casilla> getCasilla() {
		return casilla;
	}

	public void setCasilla(ArrayList<casilla> casilla) {
		this.casilla = casilla;
	}
	
	public casilla getCasilla(int pos) {
        if (pos >= 0 && pos < this.casilla.size()) {
        	return this.casilla.get(pos);
        }
        return null;
    }
	
	//MÉTODOS
	public void actualizarTablero() {
		
	}
}
