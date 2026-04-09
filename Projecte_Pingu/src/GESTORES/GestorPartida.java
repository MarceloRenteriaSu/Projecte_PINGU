package GESTORES;
import java.util.ArrayList;
import java.util.Random;

import MODELOS.*;

public class GestorPartida {

    private Partida partida;
    private GestorTablero gestorTablero;
    private GestorJugador gestorJugador;
    private Random random;

    public GestorPartida() {
        this.partida = null;
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        this.random = new Random();
    }

    public void nuevaPartida(Tablero t, ArrayList<Jugador> j) {
        this.partida = new Partida(t, j);
        System.out.println("Nova partida creada amb " + j.size() + " jugadors i un taulell de "
                + t.getTamanyo() + " caselles.");
    }

    public int tirarDado(Jugador j, Dado dadoOpcional) {
        int resultado = dadoOpcional.tirar();
        gestorJugador.jugadorSeMueve(j, resultado, this.partida.getTablero());
        System.out.println(j.getNom() + " tira el dau " + dadoOpcional.getNom()
                + " → " + resultado + " → posició " + j.getPos());
        return resultado;
    }

    public void ejecutarTurnoCompleto() {
    	if (partida != null && !partida.isFinalizada()) {
	        int indice = partida.getJugadorActual();
	        Jugador jugadorActual = partida.getJugadores().get(indice);
	        procesarTurnoJugador(jugadorActual);
	        actualizarEstadoTablero();
	        gestorTablero.comprobarFinTurno(partida);
    	}
    }

    public void procesarTurnoJugador(Jugador j) {
        if (j != null && partida != null) {
            if (j instanceof Pinguino) {
                Pinguino p = (Pinguino) j;
                if (!p.isJuega()) {
                    System.out.println(p.getNom() + " perd el seu torn.");
                    p.setJuega(true);
                } else {
                    Dado dado = new Dado("Normal", 1);
                    tirarDado(p, dado);
                    int pos = p.getPos();
                    Casilla casilla = partida.getTablero().getCasilla(pos);
                    if (casilla != null) {
                        gestorTablero.ejecutarCasilla(partida, p, casilla);
                    }
                    for (Jugador altre : partida.getJugadores()) {
                        if (altre != j && altre instanceof Pinguino) {
                            Pinguino altreP = (Pinguino) altre;
                            if (altreP.getPos() == p.getPos()) {
                                System.out.println(p.getNom() + " i " + altreP.getNom()
                                        + " coincideixen a la casella " + p.getPos() + "!");
                                gestorJugador.pinguinoGuerra(p, altreP);
                                break;
                            }
                        }
                    }
                    gestorJugador.jugadorFinalizaTurno(p);
                }
            } else if (j instanceof Foca) {
                Foca foca = (Foca) j;
                foca.moverPosicio(0);
                for (Jugador altre : partida.getJugadores()) {
                    if (altre instanceof Pinguino) {
                        Pinguino pingu = (Pinguino) altre;
                        if (foca.getPos() == pingu.getPos()) {
                            System.out.println("La Foca ha colpejat " + pingu.getNom() + "!");
                            gestorJugador.focaInteractua(pingu, foca, partida);
                        }
                    }
                }
            }
        }
    }

    public void actualizarEstadoTablero() {
    	 if (partida != null) {
	         for (Jugador j : partida.getJugadores()) {
	             int max = partida.getTablero().getTamanyo() - 1;
	             if (j.getPos() > max) {
	                 j.setPos(max);
	             }
	             if (j.getPos() < 0) {
	                 j.setPos(0);
	             }
	         }
    	 }
    }

    public void siguienteTurno() {
    	if (partida != null && !partida.isFinalizada()) {
    		partida.siguienteTurno();
    	}
    }

    public Partida getPartida() {
        return this.partida;
    }
}
