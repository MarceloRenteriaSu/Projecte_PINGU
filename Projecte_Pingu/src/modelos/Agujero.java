package modelos;

import java.util.ArrayList;

public class Agujero extends casilla {
    //CONSTRUCTOR
    public Agujero(int pos) {
        super(pos);
    }

    @Override
    public void realizarAccion(Partida partida, Pinguino p) {
        if(partida != null && p != null) {
            ArrayList<casilla> tablero = partida.getTablero().getCasilla();
            int actual = p.getPos();
            int i = 0;
            for(i = actual - 1; i >= 0; i--) {  
                if(tablero.get(i) instanceof Agujero) {
                    p.setPos(i);
                    i = -2;  
                }
            }
            if(i != -2) {  
                p.setPos(0);
            }
        }
    }
}


