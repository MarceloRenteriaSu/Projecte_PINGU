package modelos;

public class Evento extends casilla {
	//ATRIBUTOS
	protected String[] eventos;
	
	//CONSTRUCTOR
	public Evento(int pos, String[] eventos) {
		super(pos);
		this.eventos = eventos;
	}
	
	@Override
	public void realizarAccion(Partida partida, jugador jugador) {
		
	}
}
