package MODELOS;
import java.util.Random;

public class Evento extends Casilla {

	private String notificacion = "";

	public Evento(int pos) {
		super(pos);
	}

	public String getNoti() {
		return notificacion;
	}
	
	@Override
	public void realizarAccion(Partida p, Jugador j) {
		Random r = new Random();
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			Random rd = new Random();
			String evento = "";
			String[] tipos = {"Dados", "MotoDeNieve", "Bola", "Pez", "PerderT", "PerderObj"};
			int[] probs = {10, 20, 60, 80, 90, 100};
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
				int tipo = r.nextInt(10)+1;
				if(tipo > 4) {
					pingu.agregarItem(new Dado("Lento", 1));
					notificacion = pingu.getNom() + " ha obtenido 1 dado lento.";
				}else {
					pingu.agregarItem(new Dado("Rapido", 1));
					notificacion = pingu.getNom() + " ha obtinido 1 dado rapido.";
				}
				break;
			case "MotoDeNieve":
				Trineo moto = new Trineo(pingu.getPos());
				moto.realizarAccion(p, pingu);
				notificacion = "¡Moto de nieve!" + pingu.getNom() + " avanza al siguiente trineo.";
				break;
			case "Bola":
				int cantidad = r.nextInt(3)+1;
				pingu.agregarItem(new Bola(cantidad));
				notificacion = pingu.getNom() + " ha obtinido " + cantidad + " bola/s de nieve.";
				break;
			case "Pez":
				pingu.agregarItem(new Pez(1));
				notificacion = pingu.getNom() + " ha obtino 1 pez.";
				break;
			case "PerderT":
				pingu.perderTurno();
				notificacion = pingu.getNom() + " pierde 1 turno.";
				break;
			case "PerderObj":
				pingu.quitarItemAleatorio();
				notificacion = pingu.getNom() + " ha perdido 1 objeto aleatorio.";
				break;
			}	
		}
	}
}
