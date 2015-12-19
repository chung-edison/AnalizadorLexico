package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	
	private static ArrayList<ArrayList<Nodo>> stack;
	private static String[] handles;
	private static int stackpos;
		
	public Parser() {
		super();
		
		stack = new ArrayList<ArrayList<Nodo>>();
		ArrayList<Nodo> cola = new ArrayList<Nodo>();
		stack.add(cola);
		stackpos = 0;
		
		BufferedReader input = null;

		try {

			File inputFile = new File("handles.txt");

			input = new BufferedReader(new FileReader(inputFile));

			String toHand = "";
			while (input.ready()) {
				toHand += input.readLine() + "|";
			}
			handles = toHand.split("\\|");

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
	
	public Nodo shift(Nodo nuevo){

		if(nuevo.getDato().matches("EOL"))
			return new Nodo(null,null);
		
		ArrayList<Nodo> cola = stack.get(stackpos);
		cola.add(nuevo);
		
		Nodo exp = reduce(cola);
		if(exp.getInfo() != null) {
			for(Nodo nodo:cola){
				nodo.setPadre(exp);
				exp.addHijo(nodo);
			}
			cola.clear();
			if(stackpos > 0){
				stackpos--;
				exp = shift(exp);
			}
		}
		if(nuevo.getInfo().matches(";|EOF") && exp.getInfo() == null && cola.size() > 1){
			exp = new Nodo(cola.get(cola.size() - 2).getDato(), "error");
			cola.clear();
		}
		return exp;
	}

	public Nodo reduce(ArrayList<Nodo> cola){
		String acomparar = "";
		String expresion = "";
		Nodo nodo;
		int i = 0;
		
		for(i = 0; i < cola.size() - 1; i++){
			nodo = cola.get(i);
			acomparar += nodo.getInfo() + " ";
		}
		
		expresion = check(acomparar);
		
		if(expresion.matches("sub")){
			ArrayList<Nodo> aux = new ArrayList<Nodo>();
			aux.add(cola.get(cola.size() - 1));		
			cola.remove(cola.size() - 1);
			return new Nodo(null,null);
		}
		
		acomparar += cola.get(i).getInfo() + " ";

		expresion = check(acomparar);
		
		if(expresion.matches("#.*")) {
			Nodo padre = new Nodo(null, expresion);	
			return padre;
		}
		
		return new Nodo(null,null);
	}
	
	public String check(String toReduce){
		String expresion = null;
		for(String produccion:handles){
			if(produccion.matches("#.*"))
				expresion = produccion;
			if(toReduce.trim().equals(produccion))
				return expresion;
			if(produccion.matches(toReduce + "#.*"))
				return "sub";
		}
		return "";
	}
}
