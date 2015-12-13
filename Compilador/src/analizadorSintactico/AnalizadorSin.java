package analizadorSintactico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AnalizadorSin {

	private static int linea;

	public static void main(String[] args) throws IOException {

		BufferedReader input = null;

		linea = 0;

		try {

			File inputFile = new File("output.csv");

			input = new BufferedReader(new FileReader(inputFile));

			while (input.ready()) {
//				output.write(comparador.analizarLinea(input.readLine()));
//				linea++;
//				if (comparador.hayError()) {
//					System.out.println("Error sintáctico en la linea " + linea);
//					comparador.setError(false);
//				}
				System.out.println(input.readLine());
			}

//			System.out.println("Lineas analizadas: " + linea);

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
