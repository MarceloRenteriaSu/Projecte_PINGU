package clases;
import java.util.Random;

public class Evento extends Casilla {

	public Evento(int pos) {
		super(pos);
	}
	
	@Override
	public void realizarAccion(Partida p, Jugador j) {
		Random r = new Random();
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			Random rd = new Random();
			String evento = "";
			String[] tipos = {"Dados", "MotoDeNieve", "Bola", "Pez", "PerderT", "PerderObj"};
			int[] probs = {17, 34, 51, 67, 84, 100};
			int prob = rd.nextInt(100)+1;
			
			if(prob < probs[0]) {
				evento = tipos[0];
			}else if(prob < probs[1]) {
				evento = tipos[1];
			}else if(prob < probs[2]) {
				evento = tipos[2];
			}else if(prob < probs[3]) {
				evento = tipos[3];
			}else if(prob < probs[4]) {
				evento = tipos[4];
			}else{
				evento = tipos[5];
			}
			
			switch(evento) {
			case "Dados":
				int tipo = r.nextInt(3)+1;
				if(tipo > 1) {
					pingu.agregarItem(new Dado("Lento", 1));
				}else {
					pingu.agregarItem(new Dado("Rapido", 1));
				}
				break;
			case "MotoDeNieve":
				Trineo moto = new Trineo(pingu.getPos());
				moto.realizarAccion(p, pingu);
				break;
			case "Bola":
				int cantidad = r.nextInt(3)+1;
				pingu.agregarItem(new Bola(cantidad));
				break;
			case "Pez":
				pingu.agregarItem(new Pez(1));
				break;
			case "PerderT":
				//pingu.perderTurno();
				break;
			case "PerderObj":
				pingu.quitarItemAleatorio();
				break;
			}	
		
		}
	}
	
	

}
