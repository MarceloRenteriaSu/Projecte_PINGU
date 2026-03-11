package modelos;
import java.util.ArrayList;
import java.util.Random;

public class Taulell {
	protected ArrayList<Casella> caselles;
    public Taulell(int mida) {
        caselles = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < mida; i++) {
            int tipus;
            int rand = r.nextInt(100);
            if (rand < 10) {
                tipus = TipusCasella.OS;
            }else if (rand < 20) {
                tipus = TipusCasella.FORAT;
            }else if (rand < 30) {
                tipus = TipusCasella.TRINEU;
            }else if (rand < 40) {
                tipus = TipusCasella.INTERROGANT;
            }else {
                tipus = TipusCasella.NORMAL;
            }
            caselles.add(new Casella(i, tipus));
        }
    }

    public Casella getCasella(int posicio) {
        return caselles.get(posicio);
    }

    public int mida() {
        return caselles.size();
    }

}
