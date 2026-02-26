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
		if(i == null) {
			System.out.println("No se puede añadir este item.");
		}else {
			boolean encontrado = false;
			String nombre = i.getNombre();
			for(item exist : inv.getInv()) {
				if(exist.getNombre().equalsIgnoreCase(nombre)) {
					if(nombre.equalsIgnoreCase("Normal") ||
					   nombre.equalsIgnoreCase("Lento") ||
					   nombre.equalsIgnoreCase("Rapido")) {
						if(inv.contarDados() < 3) {
							exist.setCantidad(exist.getCantidad() + 1);
						}else {
							System.out.println("Maximo de dados alcanzado");
						}
					}else if(nombre.equalsIgnoreCase("Pez")) {
						if(exist.getCantidad() < 2) {
							exist.setCantidad(exist.getCantidad() + 1);
						}else {
							System.out.println("Maximo de peces alcanzado");
						}
					}else if(nombre.equalsIgnoreCase("Bola de Nieve")) {
						if(exist.getCantidad() < 6) {
							exist.setCantidad(exist.getCantidad() + 1);
						}else {
							System.out.println("Maximo de bolas alcanzado");
						}
					}
					encontrado = true;
				}
			}
			if(encontrado == false) {
				inv.getInv().add(i);
			}
		}
	}
	
	//MÉTODO QUITARITEM()
	public void quitarItem(item i, int cantidad) {
		if(i == null) {
			System.out.println("No se puede quitar este item.");
		}else {
			String nom = i.getNombre();
			for(item exist : inv.getInv()) {
				if(exist.getNombre().equalsIgnoreCase(nom) && !nom.equals("Normal")){
					if(exist.getCantidad() >= 1) {
						exist.setCantidad(exist.getCantidad() - cantidad);
					}
					if(exist.getCantidad() <= 0) {
						inv.getInv().remove(i);
					}
				}
			}  
		}
	}	
}
