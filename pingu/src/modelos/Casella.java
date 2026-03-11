package modelos;

public class Casella {
	protected int posicio;
	protected int tipus;
	public Casella(int posicio, int tipus) {
		this.posicio = posicio;
        this.tipus = tipus;
	}
	
	public int getPosicio() {
	    return posicio;
	}

    public int getTipus() {
        return tipus;
    }
}
