package clases;

public class Foca extends Jugador {
	//ATRIBUTOS
	protected boolean Soborno;
	protected int turnosBloquejada;
	//CONSTRUCTOR
	public Foca(int pos) {
		super("Foca", pos);
		this.Soborno = false;
		this.turnosBloquejada = 0;
	}
	
	public boolean isSoborno() {
		return Soborno;
	}

	public void setSoborno() {
		if(!Soborno) {
			this.Soborno = true;
			this.turnosBloquejada = 2;
		}else {
			this.Soborno = false;
			this.turnosBloquejada = 0;
		}
	}

	public int getTurnosBloquejada() {
		return turnosBloquejada;
	}

	public void setTurnosBloquejada(int turnosBloquejada) {
		this.turnosBloquejada = turnosBloquejada;
	}
	
	public int getPos() {
		return pos;
	}
	
	public void setPos(int pos) {
		this.pos = pos;
	}
	
	public void golpearPinguino(Pinguino p) {
		
	}
	
	public void aplastarPinguino(Pinguino p) {
		
	}
	
	public void aplastarJugador(Pinguino p) {
		if(p != null) {
			if(this.pos == p.getPos()) {
				p.perderMitadItems();
			}
		}
	}
	
	public void golpearJugador(Partida partida, Pinguino p) {
		if(partida != null && p != null) {
			if(this.pos == p.getPos()) {
				Casilla c = new Agujero(0);
				c.realizarAccion(partida, p);
			}
		}
	}
	
	public void esSobornado(Partida partida, Pinguino p) {
		if(p.getInv().contarItem(new Pez(0)) >= 1) {
			p.usarItem(new Pez(0));
			setSoborno();
		}else {
			golpearJugador(partida, p);
		}
	}
	
	@Override
	public void moverPosicio(int p) {
		pos = (new Dado("Normal", 0).tirar());
	}
	
	
}
