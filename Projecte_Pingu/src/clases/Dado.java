package clases;
import java.util.Random;
public class Dado extends Item {
	//Atributos
	private int min;
	private int max;
	public Dado(String nombre, int cantidad) {
		super(nombre, cantidad);
		switch(nombre) {
		case "Normal":
			this.min = 1;
			this.max = 6;
			break;
		case "Lento":
			this.min = 1;
			this.max = 3;
			break;
		case "Rapido":
			this.min = 5;
			this.max = 10;
			break;
		default:
			System.out.println("Dado no válido!");
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
        return new Random().nextInt(max - min + 1) + min;
    }
}
