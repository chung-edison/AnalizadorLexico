package com.epn;

import java.util.Arrays;

public class LexEspec {
	
	//diccionario de palabras reservadas
	private static final String[] reservadas = new String[] { "int", "float", "bool", "char", "string", "if", "then",
			"else", "while", "do", "input", "output", "return" }; 

	//diccionario de caracteres especiales
	private static final String[] especiales = new String[] { ",", ";", ":", "(", ")", "[", "]", "{", "}", "+", "-",
			"*", "/", "<", ">", "=", "!", "&", "$" };

	//diccionario de operadores compuestos
	private static final String[] compuestos = new String[] { "<=", ">=", "==", "!=", "&&", "||" };

	private boolean error;  //bandera de error para determinar la linea donde ocurrio el error
	
	String n = System.getProperty("line.separator"); //obtiene el separador de linea del sistema
	
	public boolean hayError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String analyzeLine(String line){
		String[] tokens = line.split("\\s+"); //separa el string en varios strings segun los espacios
		String result = "";
		for(String token : tokens){
			result += token + "," + checkToken(token) + n; //formato de CSV
		}
		return result;
	}
	
	public String checkToken(String token) {
		if (Arrays.asList(reservadas).contains(token)) { // palabras reservadas
			return "palabra reservada";
		} else if (Arrays.asList(especiales).contains(token)) { // caracteres especiales
			return "caracter especial";
		} else if (Arrays.asList(compuestos).contains(token)) { // operadores compuestos
			return "operador compuesto";
		} else if (token.matches("[a-z]([0-9]|[a-z]|[A-Z]|_)*")) { // identificadores
			return "identificador";
		} else if (token.matches("(-)?[0-9]*(.)[0-9]+")) { // float
			return "float";
		} else if (token.matches("(-)?[0-9]*")) { // int
			return "int";
		} else if (token.matches("(true|false)")) { // bool
			return "bool";
		} else if (token.matches("\'.\'")) { // char
			return "char";
		} else if (token.matches("\"*\"")) { // string
			return "string";
		}
		error = true;
		return "errorlex";
	}

}
