package clases;

import java.util.ArrayList;

import MODELOS.Casilla;
import MODELOS.Jugador;
import MODELOS.Partida;
import MODELOS.Pinguino;
import MODELOS.Tablero;

public class MAIN_PRUEBAS {

    public static void main(String[] args) {
    	Tablero tableroPrueba = new Tablero(50);
        for (int i = 0; i < tableroPrueba.getCasillas().size(); i++) {
            Casilla c = tableroPrueba.getCasilla(i);
            System.out.printf("Pos %2d → %s%n", i, c.getClass().getSimpleName());
        }

        ArrayList<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Pinguino("Ana", 0, "", null));

        Partida partida = new Partida(tableroPrueba, jugadores);
        System.out.println("Partida creada con " + partida.getJugadores().size() + " jugadores");
        System.out.println("Tablero de la partida tiene " + partida.getTablero().getCasillas().size() + " casillas");
    }
}