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
		item bola = new BolaDeNieve(0);
		
		if(p != null) {
			int bola1 = inv.contarItem("Bola de Nieve");
			int bola2 = p.inv.contarItem("Bola de Nieve");
			
			if(bola1 > bola2) {
				int diferencia = bola1 - bola2;
				moverPos(diferencia);
			}else if(bola1 < bola2) {
				int diferencia = bola1 - bola2;
				p.moverPos(diferencia);
			}
			quitarItem(bola);
			p.quitarItem(bola);
		}else {
			System.out.println("ERROR: No hay pingüino!");
		}
		
		
		
	}
	
	//MÉTODO USARITEM()
	public void usarItem(item i) {
		if(i == null) {
			System.out.println("No se puede usar este item.");
		}else {
			String nom = i.getNombre();
			for(item exist : inv.getInv()) {
				if(exist.getNombre().equalsIgnoreCase(nom) && !nom.equals("Normal")){
					if(exist.getCantidad() >= 1) {
						exist.setCantidad(exist.getCantidad() - 1);
					}
					if(exist.getCantidad() <= 0) {
						inv.getInv().remove(exist);
					}
				}
			}  
		}
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
	public void quitarItem(item i) {
		if(i == null) {
			System.out.println("No se puede quitar este item.");
		}else {
			item eliminar = null;
			String nombre = i.getNombre();
			for(item exist : inv.getInv()) {
				if(exist.getNombre().equalsIgnoreCase(nombre) && !nombre.equals("Normal")) {
					eliminar = exist;
				}
			}
			if(eliminar != null) {
				inv.getInv().remove(eliminar);
			}
		}
	}

	@Override
	public void moverPos(int p) {
		if(p != 0) {
			int nuevaPos = pos + p;
			if(nuevaPos < 0) {
				nuevaPos = 0;
			}
			pos = nuevaPos;
		}
	}

}
