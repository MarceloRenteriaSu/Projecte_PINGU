package modelos;
import java.util.ArrayList;
import java.util.Random;

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
	public static Tablero generarAleatorio(int cantidad) {
		ArrayList<casilla> lista = new ArrayList<>();
		if(cantidad < 50) {
        	cantidad = 50;
        }
            Random rand = new Random();
            lista.add(new Normal(0));
            for (int i = 1; i < cantidad; i++) {
                int r = rand.nextInt(100);
                if (r < 55) {
                    lista.add(new Normal(i));
                } else if (r < 65) {
                    lista.add(new Oso(i));
                } else if (r < 75) {
                    lista.add(new Agujero(i));
                } else if (r < 88) {
                    lista.add(new Trineo(i));
                } else if (r < 95) {
                    lista.add(new Evento(i));
                } else {
                    lista.add(new SueloQuebradizo(i));
                }
            }
            return new Tablero(lista);
	}
	
	public void actualizarTablero() {
		
	}
}
