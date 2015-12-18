package analizadorSintactico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AnalizadorSin {

	public static void main(String[] args) throws IOException {

		BufferedReader input = null;

		Parser parser = new Parser();

		try {

			File inputFile = new File("output.csv");

			input = new BufferedReader(new FileReader(inputFile));

			while (input.ready()) {
				String tokens[] = input.readLine().split("\",");
				parser.shift(tokens[1]);
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
