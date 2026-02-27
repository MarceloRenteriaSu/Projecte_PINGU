package modelos;
import java.util.ArrayList;
import java.util.Random;

public class Tablero {
	//ATRIBUTOS
	protected ArrayList<casilla> casilla;
	
	//CONSTRUCTOR
	public Tablero(int cantidad) {
		generarTableroAleatorio(cantidad);
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
	private casilla crearCasilla(String tipo, int pos) {
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
	
	private void generarTableroAleatorio(int cantidad) {
        Random r = new Random();
        String[] tipos = {"Normal", "Oso", "Agujero", "Trineo", "Evento", "SueloQuebradizo"};
        int[] probs = {55, 63, 73, 85, 95, 100}; 
        if(cantidad < 50) {
        	cantidad = 50;
        }
        casilla.set(0, new Normal(0));
        for (int i = 1; i <= cantidad; i++) {
            int rand = r.nextInt(100)+1;
            String tipo = "Normal";
            if(rand < probs[0]) {
            	tipo = tipos[0];
            }else if(rand < probs[1]) {
            	tipo = tipos[1];
            }else if(rand < probs[2]) {
            	tipo = tipos[2];
            }else if(rand < probs[3]) {
            	tipo = tipos[3];
            }else if(rand < probs[4]) {
            	tipo = tipos[4];
            }else {
            	tipo = tipos[5];
            }

            casilla c = crearCasilla(tipo, i);
            this.casilla.add(c);
        }
        casilla.set(cantidad, new Normal(cantidad));
    }
	
	public void actualizarTablero() {
		
	}
}
