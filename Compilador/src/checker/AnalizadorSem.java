package checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import parser.Nodo;

public class AnalizadorSem {
	
	ArrayList<String[]> simbolos;
	int contador = 0;
	
	public AnalizadorSem() {
		super();
		
		BufferedReader input = null;

		try {

			File inputFile = new File("simbolos.csv");

			input = new BufferedReader(new FileReader(inputFile));

			String simb = "";
			while (input.ready()) {
				simb += input.readLine() + "|";
			}
			String[] aux = simb.split("\\|");
			simbolos = new ArrayList<String[]>();
			for(String s:aux){
				simbolos.add(s.split(","));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String verificar(String var){
		for(String[] simb: simbolos){
			if(var.equals(simb[0])) return simb[1];
		}
		return null;
	}
	
	public String typeCheck(Nodo arbol){
		String salida = "";
		if (arbol.getInfo().matches("#FUNC")) {
			contador = 0;
		}
		if (arbol.getInfo().matches("#VAR")) {
			String tipo = arbol.getHijos().get(0).getInfo();
			if(tipo.matches("pi")) tipo = arbol.getHijos().get(1).getInfo();
			return tipo;
		} else if (arbol.getInfo().matches("#VARLOCAL")) {
			String[] varlocal;
			if (arbol.getPadre().getInfo().matches("#PARAM|#PARAMCOMA")) {
				Nodo aux = arbol;
				while (!aux.getInfo().matches("#FUNC")) {
					aux = aux.getPadre();
				}
				varlocal = new String[] { arbol.getHijos().get(2).getDato(), arbol.getHijos().get(0).getDato(),
						aux.getHijos().get(0).getHijos().get(2).getDato() };
			} else {
				varlocal = new String[] { arbol.getHijos().get(2).getDato(), arbol.getHijos().get(0).getDato() };
			}
			simbolos.add(varlocal);
			contador++;
			return "";
		} else if (arbol.getInfo().matches("#IDENT")&&!arbol.getPadre().getInfo().matches("#IDENT")) {
			String variable = arbol.getHijos().get(0).getDato();
			boolean vector = false;
			boolean funcion = false;
			if(variable == null) {
				variable = arbol.getHijos().get(0).getHijos().get(0).getDato();
				if (arbol.getHijos().get(1).getInfo().matches("ci"))
					vector = true;
				else if (arbol.getHijos().get(1).getInfo().matches("pi"))
					funcion = true;
			}
			String n = verificar(variable);
			if(n == null) System.out.println("Error linea " + arbol.getLinea() + ": \"" + variable + "\" no ha sido declarada.");
			else if(funcion){
				if(!verificarParam(arbol)){
					System.out.println("Error linea " + arbol.getLinea() + ": Tipos de parámetros no coinciden para la función \"" + variable + "\".");
				}
			} else if (n.matches(".* vect") && vector) {
				return n.split("\\s")[0];
			} else if(n.matches(".* vect")^vector){
				System.out.println("Error linea " + arbol.getLinea() + ": Uso erroneo de indexacion con \"" + variable + "\" (¿vector?).");
				return "error";
			}
			return n;
		} else if (!arbol.getHijos().isEmpty()) {
			for (Nodo hijo : arbol.getHijos())
				salida += " " + typeCheck(hijo);
		}
		if (arbol.getInfo().matches("#LOGIC")){
			salida = salida.trim();
			String[] tipos = salida.split("\\s+");
			String aux2 = inferir(tipos[0], tipos[1]);
			if(aux2.matches("error")) System.out.println("Error linea " + arbol.getLinea() + ": No se puede comparar " + tipos[0] + " con " + tipos[1]);
			salida = "";
		}
		
		if (arbol.getInfo().matches("#COM")&&arbol.getHijos().get(0).getInfo().matches("#IDENT")) {
			//System.out.println(salida);
			boolean b = false;
			Nodo Naux = arbol.getHijos().get(0).getHijos().get(0);
			if(Naux.getInfo().matches("#IDENT")) {
				Naux = Naux.getHijos().get(0);
			}
			String asigna = verificar(Naux.getDato());
			salida = salida.trim();
			String[] tipos = salida.split("\\s+");
			String aux = tipos[tipos.length - 1];
			for (int i = tipos.length - 2; i > 0; i--) {
				if (tipos[i].matches("string|char") || aux.matches("string|char")) {
					System.out.println(
							"Error linea " + arbol.getLinea() + ": No se puede transformar " + tipos[i] + " - " + aux);
					b = true;
					break;
				}
				aux = inferir(tipos[i], aux);
			}
			if (asigna != null) {
				String aux2 = inferir(asigna, aux);
				if (aux2.matches("error"))
					System.out.println("Error linea " + arbol.getLinea() + ": No se puede transformar " + aux + " a " + asigna + " (" + Naux.getDato() + ")");
			} else if (b == false&&!aux.matches("null")&&asigna==null) {
				System.out.println("\tWarning linea " + arbol.getLinea() + ": \"" + Naux.getDato() + "\" sera tratada a partir de esta linea como " + aux);
				String[] varlocal = { Naux.getDato(), aux };
				simbolos.add(varlocal);
				contador++;
			}
			salida = "";
		}

		if (arbol.getInfo().matches("#FUNC")) {  //remover variables locales
			for (; contador > 0; contador--)
				simbolos.remove(simbolos.size() - 1);
		}
		return salida;
	}
	
	public String inferir(String t1, String t2){
		if(t1.equals(t2)) return t1;
		else if(t1.matches("string|char")||t2.matches("string|char")) return "error";
		else if(t1.matches("float")||t2.matches("float")) return "float";
		else if(t1.matches("int")||t2.matches("int")) return "int";
		return "bool";
	}

	public boolean verificarParam(Nodo funcion){
		String verificar = funcion.getHijos().get(0).getHijos().get(0).getDato();
		int i = 0;
		while(!verificar.matches(simbolos.get(i)[0])){
			i++;
		}
		String[] params = expandir(funcion.getHijos().get(2)).split("\\s,\\s|\\spd");
		for(int j = 0; j < params.length - 1; j++){
			i++;
			if(!params[j].matches(simbolos.get(i)[1]))
				return false;			
		}
		return true;
	}
	
	public String expandir(Nodo nodo){
		String expand = "";
		if(nodo.getHijos().isEmpty()) {
			if(nodo.getInfo().matches("ident")){
				return verificar(nodo.getDato());
			}
			return nodo.getInfo();
		}
		for(Nodo n:nodo.getHijos()){
			expand += expandir(n) + " ";
		}
		return expand;
	}

	public void analizar(ArrayList<Nodo> arboles) {
		
		for(Nodo arbol:arboles){
			//System.out.println(arbol.mostrar());
			System.out.println(typeCheck(arbol));
		}
	
		
		//por hacer: verificar llamadas de funciones y sus argumentos
		//			 verificar vectores globales

	}
}
