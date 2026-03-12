package MODELOS;
import java.util.Random;
public class Pinguino extends Jugador {
	//ATRIBUTOS
	private String color;
	private Inventario inv;
	private boolean juega = false;
	
	//CONSTRUCTOR
	public Pinguino(String nom, int pos, String color, Inventario inv) {
		super(nom, pos);
		this.color = null;
		this.inv = inv;
	}
	
	//GETTERS Y SETTERS
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
	
	public boolean isJuega() {
		return juega;
	}

	public void setJuega(boolean juega) {
		this.juega = juega;
	}

	//MÉTODO PARA GESTIONAR LA BATALLA
	public void GestionarBatalla(Pinguino oponente) {
		if(oponente != null) {
			int nBola1 = inv.contarItem(new Bola(0));
			int nBola2 = oponente.getInv().contarItem(new Bola(0));
			if(nBola1 > nBola2) {
				int diferencia = nBola1-nBola2;
				oponente.moverPosicio(-diferencia);
			}else if(nBola2 > nBola1) {
				int diferencia = nBola2-nBola1;
				moverPosicio(-diferencia);
			}
			quitarItem(new Bola(0));
			oponente.quitarItem(new Bola(0));
		}
	}
	
	//MÉTODO PARA USAR ITEM
	public void usarItem(Item i) {
		
		if(i instanceof Pez) {
			if(inv.contarItem(i) >= 1) {
				i.setCantidad(i.getCantidad()-1);
			}
		}else if(i instanceof Dado) {
			String nombre = i.getNom();
			switch(nombre) {
			case "Normal":
					i.setCantidad(1);
				break;
			case "Rapido":
			case "Lento":
				if(inv.contarItem(i) >= 1) {
					i.setCantidad(i.getCantidad()-1);
				}
				break;
			}
		}
	}
	
	//MÉTODO PARA AGREGAR ITEMS AL INVENTARIO
	public void agregarItem(Item i) {
		if(i == null) {
			System.out.println("No se puede añadir este item.");
		}else {
			boolean encontrado = false;
			String nombre = i.getNom();
			for(Item exist : inv.getInv()) {
				if(exist.getNom().equalsIgnoreCase(nombre)) {
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
					}else if(nombre.equalsIgnoreCase("Bola")) {
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
	
	//MÉTODO PARA QUITAR ITEMS DEL INVENTARIO
	public void quitarItem(Item i) {
		if(i == null) {
			System.out.println("No se puede quitar este item.");
		}else {
			Item eliminar = null;
			String nombre = i.getNom();
			for(Item exist : inv.getInv()) {
				if(exist.getNom().equalsIgnoreCase(nombre) && !nombre.equals("Normal")) {
					eliminar = exist;
				}
			}
			if(eliminar != null) {
				inv.getInv().remove(eliminar);
			}
		}
	}
	
	//MÉTODO PARA ELIMINAR MITAD DEL INVENTARIO (ALEATORIAMENTE)
	public void perderMitadItems() {
	    int mitad = inv.totalItems() / 2;
	    for (int i = 0; i < mitad; i++) {
	        quitarItemAleatorio();
	    }
	}
	
	//MÉTODO PARA QUITAR UN ITEM ALEATORIO DEL INVENTARIO
	public void quitarItemAleatorio() {
		Random r = new Random();
		if(inv.getInv().size() >= 1) {
			int index = r.nextInt(inv.getInv().size());
			Item itemAleatorio = inv.getInv().get(index);
			itemAleatorio.setCantidad(itemAleatorio.getCantidad() - 1);
			if (itemAleatorio.getCantidad() <= 0) {
				inv.getInv().remove(index);
			}
		}
	}
	
	public void perderTurno() {
		juega = false;
	}
	
	//MÉTODO PARA MOSTRAR INVENTARIO
		public String mostrarInventario() {
			String inve = "";
			for (int i = 0; i < inv.getInv().size(); i++) {
				inve += "Nombre: " + inv.getInv().get(i).getNom() + "\nCantidad: " + inv.getInv().get(i).getCantidad() + "\n";
			}
			return inve;
		}

	@Override
	public void moverPosicio(int p) {
		if(p != 0) {
			int nuevaPos = getPos() + p;
			if(nuevaPos < 0) {
				nuevaPos = 0;
			}
			setPos(nuevaPos);
		}
	}
}
