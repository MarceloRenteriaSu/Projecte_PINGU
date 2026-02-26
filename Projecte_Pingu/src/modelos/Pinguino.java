package modelos;

public class Pinguino extends jugador {
	//ATRIBUTOS
	protected Inventario inv;
	
	//CONSTRUCTOR
	public Pinguino(String nom, int pos, Inventario inv) {
		super(nom, pos);
		this.inv = inv;
	}

	//GETTERS Y SETTERS
	public Inventario getInv() {
		return inv;
	}

	public void setInv(Inventario inv) {
		this.inv = inv;
	}
	
	//MÉTODO BATALLA()
	public void batalla(Pinguino p) {
		if(inv.contarItem(" Bola de Nieve ") > p.getInv().contarItem(" Bola de Nieve ")) {
			
		}
	}
	
	//MÉTODO USARITEM()
	public void usarItem(item i) {
		
	}
	
	private int contarDados() {
	    int total = 0;
	    for (item it : inv.getInv()) {
	        if (it.getNombre().contains("Normal")) {
	            total++;
	        }else if (it.getNombre().contains("Lento")) {
	            total++;
	        }
	        else if (it.getNombre().contains("Rapido")) {
	            total++;
	        }
	    }
	    return total;
	}
	
	//MÉTODO AGREGARITEM
	public void agregarItem(item i) {
		boolean encontrado = false;
		
		if(i == null) {
			System.out.println("No se puede añadir este item.");
		}else {
			
		}
	}
	
	//MÉTODO QUITARITEM()
	public void quitarItem(item i, int cantidad) {
		if(i == null) {
			System.out.println("No se puede quitar este item.");
		}else {
			for(item exist : inv.getInv()) {
				if(exist.getNombre().equalsIgnoreCase(i.getNombre())){
					if(exist.getCantidad() >= 1) {
						exist.setCantidad(exist.getCantidad() - cantidad);
					}else if(exist.getCantidad() <= 0) {
						inv.getInv().remove(i);
					}
				}
			}  
		}
	}

}
