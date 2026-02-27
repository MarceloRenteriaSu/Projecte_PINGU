package clases;

import java.util.Random;

public class Pinguino extends Jugador {
	protected String color;
	protected Inventario inv;
	
	public Pinguino(String nom, int pos, Inventario inv) {
		super(nom, pos);
		this.color = null;
		this.inv = inv;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Inventario getInv() {
		return inv;
	}

	public void setInv(Inventario inv) {
		this.inv = inv;
	}
	
	public void GestionarBatalla(Pinguino oponent) {
		if(oponent != null) {
			int nBola1 = inv.contarItem(new Bola(0));
			int nBola2 = oponent.getInv().contarItem(new Bola(0));
			if(nBola1 > nBola2) {
				int diferencia = nBola1-nBola2;
				moverPosicio(diferencia);
			}else if(nBola2 > nBola1) {
				int diferencia = nBola2-nBola1;
				oponent.moverPosicio(diferencia);
			}
			quitarItem(new Bola(0));
			oponent.quitarItem(new Bola(0));
		}
	}
	
	public void usarItem(Item i) {
		if(i == null) {
			System.out.println("No se puede usar este item.");
		}else {
			String nom = i.getNombre();
			for(Item exist : inv.getInv()) {
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
	
	public void agregarItem(Item i) {
		if(i == null) {
			System.out.println("No se puede añadir este item.");
		}else {
			boolean encontrado = false;
			String nombre = i.getNombre();
			for(Item exist : inv.getInv()) {
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
	
	public void quitarItem(Item i) {
		if(i == null) {
			System.out.println("No se puede quitar este item.");
		}else {
			Item eliminar = null;
			String nombre = i.getNombre();
			for(Item exist : inv.getInv()) {
				if(exist.getNombre().equalsIgnoreCase(nombre) && !nombre.equals("Normal")) {
					eliminar = exist;
				}
			}
			if(eliminar != null) {
				inv.getInv().remove(eliminar);
			}
		}
	}
	
	public void perderMitadItems() {
	    int mitad = inv.totalItems() / 2;
	    for (int i = 0; i < mitad; i++) {
	        quitarItemAleatorio();
	    }
	}
	
	public void quitarItemAleatorio() {
		Random r = new Random();
		int index = r.nextInt(inv.getInv().size());
		inv.getInv().remove(index);
	}

	@Override
	public void moverPosicio(int p) {
		if(p != 0) {
			int nuevaPos = pos + p;
			if(nuevaPos < 0) {
				nuevaPos = 0;
			}
			pos = nuevaPos;
		}
	}
	
	
}
