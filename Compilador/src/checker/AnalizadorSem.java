package checker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import parser.Nodo;

public class AnalizadorSem {
	
	ArrayList<String[]> simbolos;
	int contador = 0;
	
	public AnalizadorSem() {
		super();
		simbolos = new ArrayList<String[]>();
	}
	
	public String verificar(String var){
		if(simbolos.isEmpty()) return null;
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
			if (arbol.getPadre()!=null&&arbol.getPadre().getInfo().matches("#PARAM|#PARAMCOMA")) {
				Nodo aux = arbol;
				while (!aux.getInfo().matches("#FUNC")) {
					aux = aux.getPadre();
				}
				varlocal = new String[] { arbol.getHijos().get(2).getDato(), arbol.getHijos().get(0).getDato(),
						aux.getHijos().get(0).getHijos().get(2).getDato() };
			} else {
				varlocal = new String[] { arbol.getHijos().get(2).getDato(), arbol.getHijos().get(0).getDato()};
			}
			if(arbol.getPadre()!=null&&arbol.getPadre().getInfo().matches("#VARGLOBAL")){
				varlocal[1] += " vect";
			}
			if(verificar(varlocal[0])==null){
				simbolos.add(varlocal);
				if (arbol.getPadre()!=null&&arbol.getPadre().getInfo().matches("#LIVAR"))
					contador++;
			}else System.out.println("Error linea " + arbol.getLinea() + ": Doble declaración de \"" + varlocal[0] + "\". Se usará la primera.");
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
					System.out.println("Error linea " + arbol.getLinea() + ": Tipos o cantidad de parámetros no coinciden para la función \"" + variable + "\".");
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
			if(asigna!=null&&asigna.matches(".*vect")){
				asigna = asigna.split("\\s+")[0];
			}
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
				String aux2 = inducir(asigna, aux);
				if (aux2.matches("error"))
					System.out.println("Error linea " + arbol.getLinea() + ": No se puede transformar " + aux + " a " + asigna + " (" + Naux.getDato() + ")");
				else if (aux2.matches("warning"))
					System.out.println("Warning linea " + arbol.getLinea() + ": Se perderá precisión al transformar " + aux + " a " + asigna + " (" + Naux.getDato() + ")");
			} else if (b == false&&!aux.matches("null")&&asigna==null) {
				System.out.println("Warning linea " + arbol.getLinea() + ": \"" + Naux.getDato() + "\" sera tratada a partir de esta linea como " + aux);
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
	
	//compara los tipos respectivos
	public String inferir(String t1, String t2){
		if(t1.equals(t2)) return t1;
		else if(t1.matches("string|char")||t2.matches("string|char")) return "error";
		else if(t1.matches("float")||t2.matches("float")) return "float";
		else if(t1.matches("int")||t2.matches("int")) return "int";
		return "bool";
	}
	
	public String inducir(String aInducir, String aAsignar){
		if(aInducir.equals(aAsignar)) return aInducir;
		else if(aInducir.matches("string|char|null")||aAsignar.matches("string|char|null")) return "error";
		else if(aInducir.matches("int|bool")||aAsignar.matches("float")) {
			return "warning";
		}
		else if(aInducir.matches("float|int")||aAsignar.matches("bool")) return aInducir;
		else if(aInducir.matches("float|bool")||aAsignar.matches("int")) return aInducir;
		return "bool";
	}

	//ingresa el arbol de una llamada a una funcion para realizar la verificacion de los parametros en la llamada
	public boolean verificarParam(Nodo funcion){
		String verificar = funcion.getHijos().get(0).getHijos().get(0).getDato();
		ArrayList<String[]> params = new ArrayList<String[]>();
		for(String[] s : simbolos){
			if(s.length == 3){
				if(s[2].matches(verificar))
					params.add(s);
			}
		}
		
		String[] args = expandir(funcion.getHijos().get(2)).split("\\s+,\\s+|\\s+pd");
		if(args.length - 1 != params.size()) return false;
		for(int j = 0; j < args.length - 1; j++){
			if(!args[j].matches(params.get(j)[1]))
				return false;			
		}
		return true;
	}
	
	public int buscarFunc(String funcion){
		int i = 0;
		while(!funcion.matches(simbolos.get(i)[0])){
			i++;
		}
		return i;
	}
	
	//transforma el árbol a un string con los tipos definidos de los nodos terminales
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
			typeCheck(arbol);
		}
		
		//guardar las funciones con sus respectivos parámetros
		BufferedWriter output = null;	
		
		try {			
			File outputFile = new File("simbolos.csv");
			output = new BufferedWriter(new FileWriter(outputFile));
			for(String[] s:simbolos){
				output.write(s[0] + "," + s[1]);
				output.newLine();
			}						

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
