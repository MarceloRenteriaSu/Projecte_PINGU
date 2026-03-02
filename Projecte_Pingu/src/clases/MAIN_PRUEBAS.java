package clases;

import java.util.ArrayList;

public class MAIN_PRUEBAS {

    public static void main(String[] args) {
    	Tablero tableroPrueba = new Tablero(60);
        for (int i = 0; i < tableroPrueba.getCasillas().size(); i++) {
            Casilla c = tableroPrueba.getCasilla(i);
            System.out.printf("Pos %2d → %s%n", i, c.getClass().getSimpleName());
        }

        ArrayList<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Pinguino("Ana", 0, null));

        Partida partida = new Partida(tableroPrueba, jugadores, 0, 0);
        System.out.println("Partida creada con " + partida.getJugadores().size() + " jugadores");
        System.out.println("Tablero de la partida tiene " + partida.getTablero().getCasillas().size() + " casillas");
    }
}