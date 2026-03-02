package clases;

import java.util.ArrayList;

public class MAIN_PRUEBAS {

	public static void main(String[] args) {
		Bola b1 = new Bola(0);
		Pez p1 = new Pez(0);
		Dado d1 = new Dado("Normal", 0);
		Dado d2 = new Dado("Rapido", 1);
		Dado d3 = new Dado("Lento", 0);
		ArrayList<Item> list = new ArrayList<Item>();
		Inventario inv = new Inventario(list);
		Pinguino p = new Pinguino("marcelo", 0, inv);
		p.agregarItem(p1);
		p.agregarItem(d1);
		p.agregarItem(d2);
		p.agregarItem(d2);
		
		System.out.println(p.mostrarInventario());
		
	}

}
