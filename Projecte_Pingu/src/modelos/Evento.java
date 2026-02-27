package modelos;
import java.util.ArrayList;
import java.util.Random;
public class Evento extends casilla {
    //CONSTRUCTOR
    public Evento(int pos) {
        super(pos);
    }
    
    //METODOS
    @Override
    public void realizarAccion(Partida partida, Pinguino p) {
        if (partida == null || p == null) {
            System.out.println("ERROR");
        }else {
	        Random rand = new Random();
	        int probabilidad = rand.nextInt(100)+1;
	        String eventoElegido;
	        if (probabilidad < 31) {          //30%
	            eventoElegido = "DADO_LENTO";
	        } else if (probabilidad < 46) {   //15%
	        	eventoElegido = "MOTO_DE_NIEVE";
	        }else if  (probabilidad < 71) {	  //25%
	            eventoElegido = "BOLAS_DE_NIEVE";
	        } else if (probabilidad < 86) {   //15%
	            eventoElegido = "PEZ";
	        } else {                          //15% 
	            eventoElegido = "DADO_RAPIDO";
	        }
	        switch (eventoElegido) {
	            case "PEZ":
	                p.agregarItem(new Pez(0));
	                System.out.println(p.getNom() + " ha obtenido 1 pez!");
	                break;
	            case "BOLAS_DE_NIEVE":
	                int cantidad = rand.nextInt(3) + 1;  //1, 2 o 3
	                p.agregarItem(new BolaDeNieve(cantidad));
	                System.out.println(p.getNom() + " ha obtenido " + cantidad + " bolas de nieve!");
	                break;
	            case "DADO_RAPIDO":
	                p.agregarItem(new Dado("Rapido", 1));
	                System.out.println(p.getNom() + " ha ganado un dado RÁPIDO!");
	                break;
	            case "DADO_LENTO":
	                p.agregarItem(new Dado("Lento", 1));
	                System.out.println(p.getNom() + " ha ganado un dado LENTO!");
	                break;
	            case "MOTO_DE_NIEVE":
	            	ArrayList<casilla> tablero = partida.getTablero().getCasilla();
	    			int actual = p.getPos();
	    			for(int i = actual; i < tablero.size(); i++) {
	    				if(tablero.get(i) instanceof Trineo) {
	    					p.setPos(i);
	    					i = tablero.size() + 1;
	    				}
	    			}
	            	break;
	        }
	    }
    }
}