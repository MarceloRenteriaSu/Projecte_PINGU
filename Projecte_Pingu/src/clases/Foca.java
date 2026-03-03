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

	public void setSoborno(boolean soborno) {
		Soborno = soborno;
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
	
	@Override
	public void moverPosicio(int p) {
		pos = (new Dado("Normal", 0).tirar());
	}
	
	
}
