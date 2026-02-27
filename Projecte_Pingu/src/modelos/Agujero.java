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
            //usando boolean
           boolean encontrado = false;

            // Buscamos el agujero ANTERIOR (retrocediendo)
            for(int i = actual - 1; i >= 0; i--) {  
                if(tablero.get(i) instanceof Agujero) {
                    p.setPos(i);
                    encontrado = true;
                    i = -1;  
                }
            }

            if(! encontrado) {  
                p.setPos(1);
            
            }
        }
    }
}


