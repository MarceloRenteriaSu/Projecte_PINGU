package clases;
import java.util.ArrayList;
import java.util.Random;

public class Tablero {
	//ATRIBUTOS
	protected ArrayList<Casilla>casillas;
	
	//CONSTRUCTOR
	public Tablero(ArrayList<Casilla> casillas) {
		this.casillas = casillas;
	}
	
	public Tablero(int cantidadCasillas) {
		this.casillas = new ArrayList<>();
        generarTablero(cantidadCasillas);
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
				return new Oso(pos);
			case "Agujero":
				return new Agujero(pos);
			case "Trineo":
				return new Trineo(pos);
			case "Evento":
				return new Evento(pos);
			case "SueloQuebradizo":
				return new SueloQuebradizo(pos);
			default:
				return new Normal(pos);
		}
	}
	
	private void generarTablero(int cantidad) {
		Random r = new Random();
		String[]tipos = {"Oso", "Agujero", "Trineo", "Evento", "SueloQuebradizo"};
		int[] probs = {25, 50, 65, 80, 100};
		double[]contadores = {0, 0, 0, 0};
		if(cantidad < 50) {
			cantidad = 50;
		}
		String[]tablero = new String[cantidad];
		int restantes = (4 * (cantidad / 50))-1;
		int contador = 0;
		casillas.add(0, new Normal(0));
		tablero[0] = "Normal";

		for(int i = 1; i < cantidad-1; i++) {
			int rd = r.nextInt(100)+1;
			String tipo = "Normal";
			tablero[i] = "Normal";
			if(contador != 2) {
				tipo = "Normal";
				tablero[i] = "Normal";
				contador++;
			}else {
				if(i > 2) {
					if(rd < probs[0] && !tablero[i-3].equals(tipos[0]) && contadores[0] < restantes) {
				        tipo = tipos[0];
				        contadores[0]++;
				        tablero[i] = tipos[0];
					}else if(rd < probs[1] && !tablero[i-3].equals(tipos[1]) && contadores[1] < restantes) {
				        tipo = tipos[1];
				        contadores[1]++;
				        tablero[i] = tipos[1];
					}else if(rd < probs[2] && !tablero[i-3].equals(tipos[2]) && contadores[2] < restantes) {
				        tipo = tipos[2];
				        contadores[2]++;
				        tablero[i] = tipos[2];
					}else if(rd < probs[3] && !tablero[i-3].equals(tipos[3]) && contadores[3] < restantes) {
				        tipo = tipos[3];
				        contadores[3]++;
				        tablero[i] = tipos[3];
					}else if(rd < probs[4] && !tablero[i-3].equals(tipos[4])) {
						tipo = tipos[4];
						tablero[i] = tipos[4];
					}else {
						tipo = tipos[3];
						tablero[i] = tipos[3];
					}
				}
				contador = 0;
			}
			
			
			Casilla c = crearCasilla(tipo, i);
			casillas.add(c);
		}
		casillas.add(cantidad-1, new Normal(cantidad-1));
		
	}
	
	public void actualizarTablero() {
		
	}
	
}
