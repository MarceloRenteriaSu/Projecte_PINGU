package modelos;
import java.util.Random;
public class Evento extends casilla {
    //ATRIBUTOS
    protected String[] eventos;
    
    //CONSTRUCTOR
    public Evento(int pos, String[] eventos) {
        super(pos);
        this.eventos = eventos;
    }
    
    //METODOS
    @Override
    public void realizarAccion(Partida partida, Pinguino p) {
        if (eventos == null || eventos.length == 0) {
            System.out.println("No hay eventos definidos en esta casilla.");
        }else {
	        Random rand = new Random();
	        int probabilidad = rand.nextInt(100)+1;
	        String eventoElegido;
	        if (probabilidad < 51) {          //50%
	            eventoElegido = "DADO_LENTO";
	        } else if (probabilidad < 76) {   //25%
	            eventoElegido = "BOLAS_DE_NIEVE";
	        } else if (probabilidad < 81) {   //15%
	            eventoElegido = "PEZ";
	        } else {                          //10% 
	            eventoElegido = "DADO_RAPIDO";
	        }
	        switch (eventoElegido) {
	            case "PEZ":
	            	Pez p1 = new Pez(0);
	                p.agregarItem(p1);
	                System.out.println(p.getNom() + " ha obtenido 1 pez!");
	                break;
	            case "BOLAS_DE_NIEVE":
	                int cantidad = rand.nextInt(3) + 1;  // 1, 2 o 3
	                BolaDeNieve b = new BolaDeNieve(cantidad);
	                p.agregarItem(b);
	                System.out.println(p.getNom() + " ha obtenido " + cantidad + " bolas de nieve!");
	                break;
	            case "DADO_RAPIDO":
	                Dado r = new Dado("Rapido", 1);
	                p.agregarItem(r);
	                System.out.println(p.getNom() + " ha ganado un dado RÁPIDO!");
	                break;
	            case "DADO_LENTO":
	            	Dado l = new Dado("Lento", 1);
	                p.agregarItem(l);
	                System.out.println(p.getNom() + " ha ganado un dado LENTO!");
	                break;
	            default:
	                System.out.println("Evento desconocido: " + eventoElegido);
	                break;
	        }
	    }
    }
}