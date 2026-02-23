package modelos;
import java.util.Random;
public class Dado extends item {
	//Atributos
	private int min;
	private int max;
	public Dado(String nombre, int cantidad) {
		super(nombre, cantidad);
		if(nombre.equalsIgnoreCase("Normal")) {
			this.min = 1;
			this.max = 6;
		}else if(nombre.equalsIgnoreCase("Lento")) {
			this.min = 1;
			this.max = 3;
		}else if(nombre.equalsIgnoreCase("Rapido")) {
			this.min = 6;
			this.max = 10;
		}
	}
	
	//Getters
	public int getMin() {
		return min;
	}
	public int getMax() {
		return max;
	}
	
	//Método tirarDado()
	public int tirar() {
		Random r = new Random();
		int random = r.nextInt(max - min + 1) + min;
        return random;
    }
}
