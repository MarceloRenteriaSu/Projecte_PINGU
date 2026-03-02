package clases;
import java.util.ArrayList;
import java.util.Random;

public class Evento extends Casilla {

	public Evento(int pos) {
		super(pos);
	}
	
	@Override
	public void realizarAccion(Partida p, Jugador j) {
		
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			Random rd = new Random();
			String evento = "";
			String[] tipos = {"DadoL", "MotoDeNieve", "Bola", "Pez", "DadoR"};
			int[] probs = {31, 46, 71, 86, 100};
			int prob = rd.nextInt(100)+1;
			
			if(prob < probs[0]) {
				evento = tipos[0];
			}else if(prob < probs[1]) {
				evento = tipos[1];
			}else if(prob < probs[2]) {
				evento = tipos[2];
			}else if(prob < probs[3]) {
				evento = tipos[3];
			}else{
				evento = tipos[4];
			}
			
			switch(evento) {
			case "DadoL":
				pingu.agregarItem(new Pez(1));
			case "MotoDeNieve":
				
			case "Bola":
				
			case "Pez":
				
			case "DadoR":
			
			}	
		
		}
	}
	
	

}
