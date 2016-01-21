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
	private static boolean panico;
	
	public Parser() {
		super();
		
		stack = new ArrayList<ArrayList<Nodo>>();
		ArrayList<Nodo> cola = new ArrayList<Nodo>();
		stack.add(cola);
		stackpos = 0;
		panico = false;
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

		if(nuevo.getInfo().matches("EOL"))
			return new Nodo(null,null,0);
		
		if(nuevo.getInfo().matches("errorlex")){
			nuevo.setInfo("error");
			panico = true;
			return nuevo;
		}
		
		if(panico) return new Nodo(null,null,0);
			
		if(panico && nuevo.getInfo().matches(";|ld")){
			panico = false;
			return new Nodo(null, null,0);
		}
		
		
		ArrayList<Nodo> cola = stack.get(stackpos);
		cola.add(nuevo);
		
		Nodo exp = reduce(cola);
		if(exp.getInfo() != null) {
			for(Nodo nodo:cola){
				nodo.setPadre(exp);
				exp.addHijo(nodo);
			}
			stack.get(stackpos).clear();
			if(exp.getInfo().matches("#LICOM|#LIVAR"))
				nuevo.setInfo("");
			exp = shift(exp);
		}
		try{
			if(nuevo.getInfo().matches(";|EOF|ld")){
				if(stackpos > 0) {
					stackpos--;
					ArrayList<Nodo> colaaux = cola;
					stack.remove(stackpos + 1);
					while(!colaaux.isEmpty())
						exp = shift(colaaux.remove(0));
				} 
				if(!cola.isEmpty())
					if(stackpos == 0 && stack.get(0).size() < 3 && stack.get(0).get(0).getInfo().matches("#(?!LICOM).*")) {
						exp = stack.get(0).get(0);
						stack.get(0).clear();
					}
			}
		}catch (StackOverflowError|IndexOutOfBoundsException e){
			stack.clear();
			stack.add(new ArrayList<Nodo>());
			stackpos = 0;
			return new Nodo(nuevo.getDato(), "error", nuevo.getLinea());
		}
		if(((nuevo.getInfo().matches(";|ld") && cola.size() > 2)||(nuevo.getInfo().matches("EOF") && cola.size() > 3))&&exp.getInfo()==null){
			exp = new Nodo(cola.get(cola.size() - 2).getDato(), "error",cola.get(cola.size() - 2).getLinea());
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
			acomparar += " " + nodo.getInfo();
		}
		
		expresion = check(acomparar + " " + cola.get(i).getInfo());
		
		if(expresion == "sub") return new Nodo(null,null,0);
		
		if(expresion.matches("#.*")) {
			Nodo padre = new Nodo(null, expresion, cola.get(i).getLinea());	
			return padre;
		}
		
		if(!cola.get(i).getInfo().matches("#.*"))
			expresion = check(acomparar);
		
		if(expresion.matches("sub")&&(!cola.get(i).getInfo().matches(";"))){
			ArrayList<Nodo> aux = new ArrayList<Nodo>();
			stack.add(aux);
			stackpos++;
			return shift(cola.remove(cola.size() - 1));		
		}
		
		return new Nodo(null,null,0);
	}
	
	public String check(String toReduce){
		String expresion = null;
		for(String produccion:handles){
			if(produccion.matches("#.*"))
				expresion = produccion;
			if(toReduce.equals(produccion))
				return expresion;
			if(toReduce != "" && produccion.matches(toReduce + " #.*"))
				return "sub";
		}
		return "";
	}
}
