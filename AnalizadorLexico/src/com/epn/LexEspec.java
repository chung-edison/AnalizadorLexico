package com.epn;

import java.util.Arrays;

public class LexEspec {

	private static final String[] reservadas = new String[] { "int", "float", "bool", "char", "string", "if", "then",
			"else", "while", "do", "input", "output", "return" };

	private static final String[] especiales = new String[] { ",", ";", ":", "(", ")", "[", "]", "{", "}", "+", "-",
			"*", "/", "<", ">", "=", "!", "&", "$" };

	private static final String[] compuestos = new String[] { "<=", ">=", "==", "!=", "&&", "||" };


	public String analyzeLine(String line){
		String result = "";
		char[] array = line.toCharArray();
		
		return result;	
	}
	
	public String checkToken(String token) {
		String tokenClass = "ERROR";
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
		return tokenClass;
	}

}
