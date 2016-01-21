package checker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
			String[] varlocal = { arbol.getHijos().get(2).getDato(), arbol.getHijos().get(0).getDato() };
			simbolos.add(varlocal);
			contador++;
			return "";
		} else if (arbol.getInfo().matches("ident")&&!arbol.getPadre().getInfo().matches("#COM")) {
			String n = verificar(arbol.getDato());
			if(n == null) System.out.println("Error linea " + arbol.getLinea() + ": " + arbol.getDato() + " no ha sido declarada.");
			return n;
		} else if (!arbol.getHijos().isEmpty()) {
			for (Nodo hijo : arbol.getHijos())
				salida += " " + typeCheck(hijo);
		}
		if (arbol.getInfo().matches("#LOGIC")){
			salida = salida.trim();
			String[] tipos = salida.split("\\s+");
			String aux2 = inferir(tipos[0], tipos[1]);
			if(aux2.matches("error")) System.out.println("Error linea " + arbol.getLinea() + ": No se puede comparar " + tipos[0] + " a " + tipos[1]);
			salida = "";
		}
		
		if (arbol.getInfo().matches("#COM")&&arbol.getHijos().get(0).getInfo().matches("ident")) {
			//System.out.println(salida);
			String asigna = verificar(arbol.getHijos().get(0).getDato());
			if(asigna!=null){			
				salida = salida.trim();
				String[] tipos = salida.split("\\s+");
				String aux = tipos[tipos.length - 1];
				for(int i = tipos.length - 2; i >= 0; i--){
					if(tipos[i].matches("string|char")||aux.matches("string|char")){
						System.out.println("Error linea " + arbol.getLinea() + ": No se puede transformar " + tipos[i] + " - " + aux);
						break;
					}
					aux = inferir(tipos[i], aux);
				}
				String aux2 = inferir(asigna, aux);
				if(aux2.matches("error")) System.out.println("Error linea " + arbol.getLinea() + ": No se puede transformar " + aux + " a " + asigna + "(" + arbol.getHijos().get(0).getDato() + ")");
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

	public void analizar(ArrayList<Nodo> arboles) {
		
		for(Nodo arbol:arboles){
			//System.out.println(arbol.terminales());
			System.out.println(typeCheck(arbol));
		}
	
		
		//por hacer: verificar llamadas de funciones y sus argumentos
		//			 verificar vectores globales

	}
}
