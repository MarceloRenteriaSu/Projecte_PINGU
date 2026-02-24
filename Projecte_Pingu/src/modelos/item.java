package modelos;

public abstract class item {
	//Atributos
	protected String nombre;
    protected int cantidad;

    //Constructor
    public item(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    //Getters y Setters
    public String getNombre() { 
    	return nombre; 
    }
    
    public void setNombre(String nombre) {
    	this.nombre = nombre;
    }
    
    public int getCantidad() { 
    	return cantidad; 
    }
    public void setCantidad(int cantidad) { 
    	this.cantidad = cantidad; 
    }
}
