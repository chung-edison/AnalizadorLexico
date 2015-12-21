package bin;

import lexer.AnalizadorLex;
import parser.AnalizadorSin;

public class Compilador {

	public static void main(String[] args) {
		AnalizadorLex lexer = new AnalizadorLex();
		AnalizadorSin parser = new AnalizadorSin();
		//AnalizadorSem typecheck = new AnalizadorSem();
		
		lexer.lex();
		parser.parse();		
		//typecheck.typeCheck();		
		
		System.out.println("Compilación completa");

	}
}
