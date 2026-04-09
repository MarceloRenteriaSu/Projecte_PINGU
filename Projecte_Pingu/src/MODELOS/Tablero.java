package MODELOS;

import java.util.ArrayList;
import java.util.Random;

public class Tablero {
	// ATRIBUTOS
	private ArrayList<Casilla> casillas;
	private Random r;
	private int tamanyo;

	// CONSTRUCTOR

	public Tablero(int cantidadCasillas) {
		this.r = new Random();
		this.casillas = new ArrayList<>();
		if (cantidadCasillas < 50) {
			this.tamanyo = 50;
		} else if (cantidadCasillas > 150) {
			this.tamanyo = 150;
		} else {
			this.tamanyo = cantidadCasillas;
		}
		generarTablero(this.tamanyo);
	}

	// GETTERS Y SETTERS
	public int getTamanyo() {
		return casillas.size();
	}

	public ArrayList<Casilla> getCasillas() {
		return new ArrayList<>(casillas);
	}

	public void setCasillas(ArrayList<Casilla> casillas) {
		this.casillas = casillas;
	}

	public Casilla getCasilla(int pos) {
		if (pos >= 0 && pos < casillas.size()) {
			return casillas.get(pos);
		}
		return null;
	}

	// MÉTODO DE CREAR CASILLA
	private Casilla crearCasilla(String tipo, int pos) {
		switch (tipo) {
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
		// Tipus possibles per a caselles intermèdies
		String[] tipos = { "Normal", "Oso", "Agujero", "Trineo", "Evento", "SueloQuebradizo" };
		int[] probs = { 15, 30, 45, 65, 85, 100 };

		if (cantidad < 50) {
			cantidad = 50;
		}

		// Primera casella: sempre Normal
		casillas.add(new Normal(0));

		// Caselles intermèdies: totes aleatòries
		for (int i = 1; i < cantidad - 1; i++) {
			int rd = r.nextInt(100) + 1;
			String tipo = tipos[tipos.length - 1];
			for (int t = 0; t < probs.length; t++) {
				if (rd <= probs[t]) {
					tipo = tipos[t];
					break;
				}
			}
			casillas.add(crearCasilla(tipo, i));
		}

		// Última casella: sempre Normal
		casillas.add(new Normal(cantidad - 1));
	}

	// MÉTODO BUSCAR ULTIMO AGUJERO
	public int agujeroAnterior(int posActual) {
		for (int i = posActual - 1; i >= 0; i--) {
			if (casillas.get(i) instanceof Agujero) {
				return i;
			}
		}
		return -1;
	}

	// MÉTODO BUSCAR ULTIMO AGUJERO
	public int trineoPosterior(int posActual) {
		for (int i = posActual + 1; i < casillas.size(); i++) {
			if (casillas.get(i) instanceof Trineo) {
				return i;
			}
		}
		return -1;
	}

}
