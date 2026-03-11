package modelos;

public class Jugador {
    private String id;        // Nom o identificador
    private String color;     // Per representació al taulell
    private int posicio;      // Casella actual
    private Inventari inventari;

    public Jugador(String id, String color) {
        this.id = id;
        this.color = color;
        this.posicio = 0; // Comencen a la casella 0
        this.inventari = new Inventari();
    }

    // Mètodes de moviment
    public void moureA(int novaPosicio) {
        if (novaPosicio < 1) {
            this.posicio = 1;
        } else {
            this.posicio = novaPosicio;
        }
    }

    public void tornarAInici() {
        this.posicio = 0;
    }

    // Accés a l'inventari
    public Inventari getInventari() {
        return inventari;
    }

    // Getters bàsics
    public String getId() { return id; }
    public String getColor() { return color; }
    public int getPosicio() { return posicio; }
    
    @Override
    public String toString() {
        return "Jugador: " + id + " [" + color + "] a la casella " + posicio;
    }
}