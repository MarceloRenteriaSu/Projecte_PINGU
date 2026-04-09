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

	/**
	 * La Foca passa per sobre d'un pingüí de passada.
	 * El pingüí perd la meitat del seu inventari.
	 */
	public void golpearJugador(Partida p, Pinguino pingu) {
		if(p != null && pingu != null) {
			pingu.perderMitadItems();
		}
	}

	/**
	 * La Foca s'atura a la mateixa casella que un pingüí.
	 * - Si el pingüí té peixos: els utilitza per subornar la Foca.
	 * - Si no té peixos: el pingüí és enviat a l'Agujero anterior o a l'inici.
	 */
	public void aplastarJugador(Partida partida, Pinguino pingu) {
		if(partida != null && pingu != null) {
			if(pingu.getInv().contarItem(new Pez(0)) >= 1) {
				pingu.quitarItem(new Pez(0));
				esSobornado(partida, pingu);
			} else {
				int posActual = pingu.getPos();
				int posAgujeroAnterior = partida.getTablero().agujeroAnterior(posActual);
				int nuevaPos = (posAgujeroAnterior >= 0) ? posAgujeroAnterior : 0;
				pingu.setPos(nuevaPos);
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
