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
		for(String[] n:simbolos){
			if(var.equals(n[0])) return n[1];
		}
		return null;
	}

	public void typeCheck(ArrayList<Nodo> arboles) {
	
		//por hacer: busqueda en profundidad de expresiones logicas y aritmeticas en cada arbol
		//cambiar el agregado de variables del parser al checker
		//eliminar variables locales de la lista de simbolos una vez terminado el bloque
		//retorno de errores

	}
}
