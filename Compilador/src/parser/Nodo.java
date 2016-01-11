package parser;

import java.util.ArrayList;

public class Nodo {
	
	private String dato;
	private String info;
	private Nodo padre;
	private ArrayList<Nodo> hijos;
		
	public Nodo(String dato, String info) {
		super();
		this.dato = dato;
		this.info = info;
		this.padre = null;
		this.hijos = new ArrayList<Nodo>();
	}
		
	public String getDato() {
		return dato;
	}

	public void setDato(String dato) {
		this.dato = dato;
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Nodo getPadre() {
		return padre;
	}

	public void setPadre(Nodo padre) {
		this.padre = padre;
	}

	public ArrayList<Nodo> getHijos() {
		return hijos;
	}

	public void addHijo(Nodo hijo) {
		this.hijos.add(hijo);
	}

	public boolean esTerminal(){
		return hijos.isEmpty();
	}
	
	public String mostrar(){
		String salida = this.info;
		if (!hijos.isEmpty()) {
			salida += "(";
			for (Nodo nodo : hijos)
				salida += nodo.mostrar() + " ";
			salida = salida.trim() + ")";
		}
		return salida;
	}

}
