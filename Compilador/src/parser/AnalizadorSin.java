package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AnalizadorSin {

	private int linea = 1;

	public void parse() {

		Parser parser = new Parser();
		
		BufferedReader input = null;
		BufferedWriter output = null;

		try {

			File inputFile = new File("output.csv");

			input = new BufferedReader(new FileReader(inputFile));
			
			File outputFile = new File("simbolos.csv");

			output = new BufferedWriter(new FileWriter(outputFile));
			
			Nodo arbol;

			while (input.ready()) {
				String tokens[] = input.readLine().split("\",");
				
				if(tokens[1].matches("EOL")){
					linea++;
				}
				
				arbol = parser.shift(new Nodo(tokens[0].substring(1), tokens[1]));
				
				if(arbol.getInfo() != null){
					if(arbol.getInfo().equals("#VARGLOBAL")){
						Nodo aux = arbol.getHijos().get(2);
						output.write(aux.getDato() + ",");
						aux = arbol.getHijos().get(0);
						output.write(aux.getDato());
						output.newLine();
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
				if (output != null)
					output.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
