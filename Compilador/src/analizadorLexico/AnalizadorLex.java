package analizadorLexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AnalizadorLex {

	private static int linea;

	public static void main(String[] args) throws IOException {

		ComparadorLex comparador = new ComparadorLex();

		BufferedReader input = null;
		BufferedWriter output = null;

		linea = 0;

		try {

			File inputFile = new File("input.txt");

			input = new BufferedReader(new FileReader(inputFile));

			File outputFile = new File("output.csv");

			output = new BufferedWriter(new FileWriter(outputFile));

			while (input.ready()) {
				output.write(comparador.analizarLinea(input.readLine()));
				linea++;
				if (comparador.hayError()) {
					System.out.println("Error lexico en la linea " + linea);
					comparador.setError(false);
				}
			}
			
			output.write("\"\",EOF");

			System.out.println("Lineas analizadas: " + linea);

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
