package com.epn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Lex {

	private static int linea;

	public static void main(String[] args) throws IOException {

		BufferedReader input = null;
		linea = 0;

		try {

			File inputFile = new File("input.txt");

			input = new BufferedReader(new FileReader(inputFile));

			while (input.ready()) {
				System.out.println(input.readLine());
				linea++;
			}

			System.out.println(linea);

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

		BufferedWriter output = null;

		try {

			File outputFile = new File("output.txt");
			output = new BufferedWriter(new FileWriter(outputFile));
			output.newLine();
			
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
