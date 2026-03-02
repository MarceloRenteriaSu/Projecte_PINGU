package clases;

import java.util.ArrayList;

public class MAIN_PRUEBAS {

    public static void main(String[] args) {
        
        System.out.println("=== PRUEBAS JUEGO PINGÜINOS EN EL HIELO ===\n");
        
        // 1. Crear algunos ítems
        Pez pez1 = new Pez(1);
        Pez pez2 = new Pez(1);
        Bola bola1 = new Bola(3);
        Bola bola2 = new Bola(2);
        Dado normal1 = new Dado("Normal", 1);
        Dado rapido1  = new Dado("Rapido", 1);
        Dado lento1   = new Dado("Lento", 1);

        // 2. Crear inventarios
        ArrayList<Item> itemsP1 = new ArrayList<>();
        ArrayList<Item> itemsP2 = new ArrayList<>();
        ArrayList<Item> itemsP3 = new ArrayList<>();

        Inventario invP1 = new Inventario(itemsP1);
        Inventario invP2 = new Inventario(itemsP2);
        Inventario invP3 = new Inventario(itemsP3);

        // 3. Crear pingüinos (jugadores)
        Pinguino pingu1 = new Pinguino("Tux",    0, invP1);
        Pinguino pingu2 = new Pinguino("Pingo",  0, invP2);
        Pinguino pingu3 = new Pinguino("Chilly", 0, invP3);

        // 4. Añadir ítems (probando límites)
        System.out.println("--- Añadiendo ítems ---");
        
        pingu1.agregarItem(pez1);
        pingu1.agregarItem(pez2);           // 2 peces → límite
        pingu1.agregarItem(pez1);           // debería decir "Máximo de peces alcanzado"

        pingu1.agregarItem(bola1);          // 3 bolas
        pingu1.agregarItem(bola2);          // +2 → total 5

        pingu1.agregarItem(normal1);
        pingu1.agregarItem(rapido1);
        pingu1.agregarItem(lento1);
        pingu1.agregarItem(new Dado("Normal", 1));   // debería decir máximo dados

        System.out.println("\nInventario de " + pingu1.getNom() + " después de agregar:");
        System.out.println(pingu1.mostrarInventario());

        // 5. Crear tablero pequeño para pruebas rápidas
        ArrayList<Casilla> casillas = new ArrayList<>();
        
        // Pos 0 siempre Normal
        casillas.add(new Normal(0));
        
        // Creamos manualmente un tablero de prueba (más controlado que random)
        casillas.add(new Normal(1));
        casillas.add(new Agujero(2));
        casillas.add(new Normal(3));
        casillas.add(new Trineo(4));
        casillas.add(new SueloQuebradizo(5));
        casillas.add(new Oso(6));
        casillas.add(new Evento(7));
        casillas.add(new Trineo(8));
        casillas.add(new Normal(9));
        casillas.add(new Normal(10));   // meta ficticia

        Tablero tableroPrueba = new Tablero(casillas);
        
        // 6. Crear partida
        ArrayList<Jugador> jugadores = new ArrayList<>();
        jugadores.add(pingu1);
        jugadores.add(pingu2);
        jugadores.add(pingu3);

        Partida partida = new Partida(tableroPrueba, jugadores, 0, 0);

        System.out.println("\n--- Tablero de prueba creado (tamaño: " + casillas.size() + ") ---");
        for (Casilla c : casillas) {
            String tipo = c.getClass().getSimpleName();
            System.out.printf("Pos %2d → %s\n", c.getPos(), tipo);
        }

        // 7. Pruebas de movimiento y casillas especiales
        System.out.println("\n--- Pruebas de casillas especiales ---");

        // Simulamos que pingu1 cae en agujero (pos 2)
        pingu1.setPos(2);
        System.out.println(pingu1.getNom() + " está en pos " + pingu1.getPos() + " (Agujero)");
        casillas.get(2).realizarAccion(partida, pingu1);
        System.out.println("→ Después del agujero: posición = " + pingu1.getPos());

        // Simulamos trineo (pos 4)
        pingu2.setPos(4);
        System.out.println(pingu2.getNom() + " está en pos " + pingu2.getPos() + " (Trineo)");
        casillas.get(4).realizarAccion(partida, pingu2);
        System.out.println("→ Después del trineo: posición = " + pingu2.getPos());

        // Suelo quebradizo – con muchos ítems
        pingu1.setPos(5);
        System.out.println(pingu1.getNom() + " está en Suelo Quebradizo con " + 
                           pingu1.getInv().totalItems() + " ítems");
        casillas.get(5).realizarAccion(partida, pingu1);
        System.out.println("→ Después: posición = " + pingu1.getPos());

        // 8. Prueba de batalla de bolas
        System.out.println("\n--- Prueba de batalla de bolas ---");
        pingu2.agregarItem(new Bola(4));
        pingu3.agregarItem(new Bola(1));

        System.out.println("Bolas → " + pingu1.getNom() + ": " + pingu1.getInv().contarItem(new Bola(0)));
        System.out.println("Bolas → " + pingu2.getNom() + ": " + pingu2.getInv().contarItem(new Bola(0)));
        System.out.println("Bolas → " + pingu3.getNom() + ": " + pingu3.getInv().contarItem(new Bola(0)));
        
        System.out.println("Posición antes de batalla:");
        System.out.println(pingu2.getNom() + " → pos " + pingu2.getPos());
        System.out.println(pingu3.getNom() + " → pos " + pingu3.getPos());
        
        System.out.println("\n" + pingu2.getNom() + " ataca a " + pingu3.getNom());
        pingu2.GestionarBatalla(pingu3);
        
        System.out.println("Posición después de batalla:");
        System.out.println(pingu2.getNom() + " → pos " + pingu2.getPos());
        System.out.println(pingu3.getNom() + " → pos " + pingu3.getPos());
        System.out.println("Bolas → " + pingu1.getNom() + ": " + pingu1.getInv().contarItem(new Bola(0)));
        System.out.println("Bolas → " + pingu2.getNom() + ": " + pingu2.getInv().contarItem(new Bola(0)));
        System.out.println("Bolas → " + pingu3.getNom() + ": " + pingu3.getInv().contarItem(new Bola(0)));

        // 9. Prueba de perder mitad de ítems
        System.out.println("\n--- Prueba perder mitad ítems ---");
        System.out.println("Antes: " + pingu1.getNom() + " tiene " + pingu1.getInv().totalItems() + " ítems");
        pingu1.perderMitadItems();
        System.out.println("Después: " + pingu1.getNom() + " tiene " + pingu1.getInv().totalItems() + " ítems");
        System.out.println(pingu1.mostrarInventario());

        // 10. Tirar dados de ejemplo
        System.out.println("\n--- Ejemplo de tiradas de dados ---");
        Dado dNormal = new Dado("Normal", 0);
        Dado dRapido = new Dado("Rapido", 0);
        Dado dLento  = new Dado("Lento",  0);

        for (int i = 0; i < 8; i++) {
            System.out.printf("Tirada %d: Normal=%2d | Rápido=%2d | Lento=%2d\n",
                    i+1, dNormal.tirar(), dRapido.tirar(), dLento.tirar());
        }

        System.out.println("\n=== FIN DE LAS PRUEBAS ===\n");
    }
}