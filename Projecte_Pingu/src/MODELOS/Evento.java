package MODELOS;
import java.util.Random;

public class Evento extends Casilla {

	// Guardem l'últim event que ha passat per mostrar-lo a la UI
	private String ultimoEventoDescripcion = "";

	public Evento(int pos) {
		super(pos);
	}

	public String getUltimoEventoDescripcion() {
		return ultimoEventoDescripcion;
	}
	
	@Override
	public void realizarAccion(Partida p, Jugador j) {
		Random r = new Random();
		if(j instanceof Pinguino) {
			Pinguino pingu = (Pinguino) j;
			Random rd = new Random();
			String evento = "";
			String[] tipos = {"Dados", "MotoDeNieve", "Bola", "Pez", "PerderT", "PerderObj"};
			int[] probs = {15, 30, 60, 80, 90, 100};
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
					ultimoEventoDescripcion = "🎲 Ha obtingut un Dado Lento!";
				}else {
					pingu.agregarItem(new Dado("Rapido", 1));
					ultimoEventoDescripcion = "🎲 Ha obtingut un Dado Ràpid!";
				}
				break;
			case "MotoDeNieve":
				Trineo moto = new Trineo(pingu.getPos());
				moto.realizarAccion(p, pingu);
				ultimoEventoDescripcion = "🛷 Moto de Neu! Avança fins al pròxim trineo!";
				break;
			case "Bola":
				int cantidad = r.nextInt(3)+1;
				pingu.agregarItem(new Bola(cantidad));
				ultimoEventoDescripcion = "❄ Ha obtingut " + cantidad + " Bola(es) de Neu!";
				break;
			case "Pez":
				pingu.agregarItem(new Pez(1));
				ultimoEventoDescripcion = "🐟 Ha obtingut un Peix!";
				break;
			case "PerderT":
				pingu.perderTurno();
				ultimoEventoDescripcion = "⏭ Perd el pròxim torn!";
				break;
			case "PerderObj":
				pingu.quitarItemAleatorio();
				ultimoEventoDescripcion = "💔 Ha perdut un objecte aleatori!";
				break;
			}	
		}
	}
}
