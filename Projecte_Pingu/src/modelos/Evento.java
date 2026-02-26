package modelos;

import java.util.Random;  // ← Import OBLIGATORIO

public class Evento extends casilla {  // ← Corregido: Casilla con mayúscula

    // ATRIBUTOS
    protected String[] eventos;

    // CONSTRUCTOR
    public Evento(int pos, String[] eventos) {
        super(pos);
        this.eventos = eventos;
    }
    //METODOS

    @Override
    public void realizarAccion(Partida partida, jugador jugador) {
        if (eventos == null || eventos.length == 0) {
            System.out.println("No hay eventos definidos en esta casilla.");
            return;
        }

        Random rand = new Random();

        int probabilidad = rand.nextInt(100);
        String eventoElegido;

        if (probabilidad < 50) {          // 50% DADO_LENTO(ALTA PROBABILIDAD) 
            eventoElegido = "DADO_LENTO";
        } else if (probabilidad < 80) {   // 30% BOLAS_DE_NIEVE
            eventoElegido = "BOLAS_DE_NIEVE";
        } else if (probabilidad < 92) {   // 12% PEZ
            eventoElegido = "PEZ";
        } else {                          // 8%  DADO_RAPIDO
            eventoElegido = "DADO_RÁPIDO";
        }

        // Ejecutar el efecto correspondiente
        switch (eventoElegido) {
            case "PEZ":
                jugador.getInventario().agregarPez(1);
                System.out.println(jugador.getNom() + " ha obtenido 1 pez ");
                break;

            case "BOLAS_DE_NIEVE":
                int cantidad = rand.nextInt(3) + 1;  // 1, 2 o 3
                jugador.getInventario().agregarBolaDeNieve(cantidad);
                System.out.println(jugador.getNom() + " ha obtenido " + cantidad + " bolas de nieve ");
                break;

            case "DADO_RAPIDO":
                int avance = rand.nextInt(6) + 5;  // 5 a 10 casillas
                partida.moverJugador(jugador, avance);
                System.out.println(jugador.getNom() + " ha sacado dado RÁPIDO! + " + avance + " casillas ");
                break;

            case "DADO_LENTO":
                int avanceLento = rand.nextInt(3) + 1;  // 1 a 3 casillas
                partida.moverJugador(jugador, avanceLento);
                System.out.println(jugador.getNom() + " ha sacado dado LENTO + " + avanceLento + " casillas");
                break;

            default:
                System.out.println("Evento desconocido: " + eventoElegido);
                break;
        }
    }
}