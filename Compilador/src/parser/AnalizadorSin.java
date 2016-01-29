package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AnalizadorSin {

	private int linea = 1;
	private ArrayList<Nodo> arboles;

	public ArrayList<Nodo> getArboles() {
		return arboles;
	}

	public void parse() {

		Parser parser = new Parser();
		arboles = new ArrayList<Nodo>();
		
		BufferedReader input = null;

		try {

			File inputFile = new File("output.csv");

			input = new BufferedReader(new FileReader(inputFile));
			
			Nodo arbol;

			while (input.ready()) {
				String tokens[] = input.readLine().split("\",(?!\")");
				
				if(tokens[1].matches("EOL")){
					linea++;
				}
				
				Nodo sig = new Nodo(tokens[0].substring(1), tokens[1], linea);
				arbol = parser.shift(sig);
				
				if(arbol.getInfo() != null){
					//System.out.println(arbol.mostrar());

					if(arbol.getInfo().matches("#VARLOCAL|#VARGLOBAL|#COM|#BLOQ|#FUNC")){
						arboles.add(arbol);
					}
					
					if(arbol.getInfo().equals("error")){
						System.out.println("Error en linea " + linea + " cerca de: " + arbol.getDato());
					}
				}
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

}
