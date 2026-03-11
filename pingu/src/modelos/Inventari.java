package modelos;

public class Inventari {
    private int peixos;         
    private int bolesNeu;       
    private int dausRapids;      
    private int dausLents;

    public Inventari() {
        this.peixos = 0;
        this.bolesNeu = 0;
        this.dausRapids = 0;
        this.dausLents = 0;
    }

    //MÊTODE PER AFEGIR COM A MAXIM 2 PEIXOS
    public boolean afegirPeix() {
        if (peixos < 2) {
            peixos++;
            return true;
        }
        return false;
    }
    
    //MÈTODE POR ELIMINAR 1 PEIX
    public void consumPeix() {
        if (peixos > 0) {
        	peixos--;
        }
    }

    //MÈTODE PER AFEGIR COM A MAXIM 6 BOLES DE NEU
    public void afegirBolesNeu(int quantitat) {
        this.bolesNeu += quantitat;
        if (this.bolesNeu > 6) {
        	this.bolesNeu = 6;
        }
    }

    //MÈTODE PER AGEFIR COM A MAXIM 3 DAUS ESPECIALS
    public boolean afegirDau(String tipus) {
        if ((dausRapids + dausLents) < 3) {
            if (tipus.equals("RAPID")) {
            	dausRapids++;
            }else{
            	dausLents++;
            }
            return true;
        }
        return false;
    }

    // Getters per a la base de dades
    public int getPeixos() {
    	return peixos;
    }
    
    public int getBolesNeu() {
    	return bolesNeu;
    }
    
    public int getDausRapids() {
    	return dausRapids;
    }
    
    public int getDausLents() {
    	return dausLents;
    }
}
