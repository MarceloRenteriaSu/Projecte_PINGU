package clases;
import java.util.Random;

public class Evento extends Casilla {

	public Evento(int pos) {
		super(pos);
	}
	
	@Override
	public void realizarAccion(Partida p, Jugador j) {
		Random r = new Random();
		String tipos = {"DadoL", "DadoR", "MotoDeNieve", "", ""};
		
	}
	
	

}
