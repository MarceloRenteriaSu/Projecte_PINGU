package MODELOS;

public class Foca extends Jugador {
	//ATRIBUTOS
	private boolean Soborno;
	private int turnosBloquejada;
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
	
	public void aplastarJugador(Pinguino p) {
		p.perderMitadItems();
	}
	
	public void golpearJugador(Partida p, Pinguino pingu) {
		if(p != null && pingu != null) {
			int posActual = pingu.getPos();
			int posAgujeroAnterior = p.getTablero().agujeroAnterior(posActual);
			int nuevaPos;
			if(posAgujeroAnterior >= 0) {
				nuevaPos = posAgujeroAnterior;
			}else {
				nuevaPos = 0;
			}
			if(pingu.getInv().contarItem(new Pez(0)) >= 1) {
				pingu.usarItem(new Pez(0));
				esSobornado(p, pingu);
			}else {
				pingu.setJuega(true);
				pingu.setPos(nuevaPos);
				pingu.perderTurno();
				
			}
		}
	}
	
	public void esSobornado(Partida partida, Pinguino p) {
		this.Soborno = true;
		this.turnosBloquejada = 2;
	}
	
	@Override
	public void moverPosicio(int p) {
		if(turnosBloquejada > 0) {
			turnosBloquejada--;
			if(turnosBloquejada == 0) {
				Soborno = false;
			}
		} else {
			int tirada = new Dado("Normal", 0).tirar();
			setPos(getPos() + tirada);
		}
	}
	
	
}
