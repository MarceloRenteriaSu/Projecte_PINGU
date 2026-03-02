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
		String[]tipos = {"Normal", "Oso", "Agujero", "Trineo", "Evento", "SueloQuebradizo"};
		int[] probs = {45, 60, 72, 84, 94, 100};
		if(cantidad < 50) {
			cantidad = 50;
		}
		casillas.add(0, new Normal(0));
		for(int i = 1; i <= cantidad; i++) {
			int rd = r.nextInt(100)+1;
			String tipo = "Normal";
			if(rd < probs[0]) {
				tipo = tipos[0];
			}else if(rd < probs[1]) {
				tipo = tipos[1];
			}else if(rd < probs[2]) {
				tipo = tipos[2];
			}else if(rd < probs[3]) {
				tipo = tipos[3];
			}else if(rd < probs[4]) {
				tipo = tipos[4];
			}else {
				tipo = tipos[5];
			}
			
			Casilla c = crearCasilla(tipo, i);
			casillas.add(c);
		}
		casillas.set(cantidad, new Normal(cantidad));
		
	}
	
	public void actualizarTablero() {
		
	}
	
}
