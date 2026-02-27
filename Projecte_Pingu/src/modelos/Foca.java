package modelos;
public class Foca extends jugador {
	//ATRIBUTOS
	protected boolean soborno;
	protected int turnosBloqueada;
	
	//CONSTRUCTOR
	public Foca(int pos) {
		super("Foca", pos);
		this.soborno = false;
		this.turnosBloqueada = 0;
	}
	
	//GETTERS Y SETTERS
	public boolean isSoborno() {
		return soborno;
	}

	public void setSoborno() {
		if(soborno == false) {
			this.soborno = true;
			this.turnosBloqueada = 2;
		}else {
			this.soborno = false;
		}
	}

	//MÉTODO APLASTARJUGADOR()
	public void aplastarJugador(Pinguino p) {
		
	}
	
	//MÉTODO GOLPEARJUGADOR()
	public void golpearJugador(Partida partida, Pinguino p) {
		if(partida != null & p != null) {
			if(this.pos == p.getPos()) {
				
			}
		}
	}
	
	//MÉTODO ESSOBORNADO()
	public void esSobornado(Partida partida, Pinguino p) {
		if(p.getInv().contarItem("Pez") >= 1) {
			p.usarItem(new Pez(0));
			setSoborno();
		}else {
			golpearJugador(partida, p);
		}
	}
}
