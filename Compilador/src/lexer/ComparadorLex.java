package lexer;

public class ComparadorLex {
	
	//diccionario de palabras reservadas
	private static final String tipos = "int|float|bool|char|string";
			
	private static final String reservadas = "if|then|else|while|do|input|output|return"; 

	//diccionario de caracteres especiales
	private static final String especiales = ",|;|:|=|!|&|\\$";
	
	private static final String signos = "\\+|-|\\*|/|<|>";

	//diccionario de operadores compuestos
	private static final String opcomp = "<=|>=|==|!=|&&|\\|\\|";
	
	private static final String nosignos = ",|;|:|\\$";
	
	private static final String texto = "(?:\".*\")|([0-9]|[a-z]|[A-Z]|_)+";
	
	private static final String numeros = "(-)?[0-9]+((\\.)[0-9]+)?";

	private boolean error;  //bandera de error para determinar la linea donde ocurrio el error
	
	private boolean comment;	//bandera para ignorar comentarios
	
	private String n = System.getProperty("line.separator"); //obtiene el separador de linea del sistema
	
	public boolean hayError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String analizarLinea(String line){
		if(line.matches(".*\\*/")) {
			comment = false;
			return "";
		}
		if(line.matches("//.*")||comment) return "";
		if(line.matches("/\\*.*")) comment = true;
		line = line.replaceAll(numeros + "|" + texto + "|" + nosignos, " $0 ").trim(); 
			//agrega espacios antes de cada id y num, es decir, identifica tokens y separa
		
		//System.out.println(line); //muestra la linea luego de haber añadido espacios
		String[] tokens = line.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); //separa el string en varios strings segun los espacios
																			//toma en cuenta que haya 0 o un numero par de comillas
		
		String result = "";
		for(String token : tokens){
			result += "\"" + token + "\"" + "," + tokenClass(token) + n; //formato de CSV
		}
		result += "\"EOL\",EOL" + n;
		return result;
	}

	public String tokenClass(String token) {
		if (token.matches("\".*\"")) { // string
			return "string";
		} else if (token.matches("\'.\'")) { // char
			return "char";
		} else if (token.matches(tipos)) { // palabras reservadas
			return "tipo";
		} else if (token.matches(reservadas)) { // palabras reservadas
			return "reserv";
		} else if (token.matches("(true|false)")) { // bool
			return "bool";
		} else if (token.matches(opcomp)) { // operadores compuestos
			return "opcomp";
		} else if (token.matches(signos)) { // operadores simples
			return "signo";
		} else if (token.matches(especiales)) { // caracteres especiales
			return token;	
		} else if (token.matches("\\(")) { // caracteres especiales
			return "pi";	
		} else if (token.matches("\\)")) { // caracteres especiales
			return "pd";	
		} else if (token.matches("\\[")) { // caracteres especiales
			return "ci";	
		} else if (token.matches("\\]")) { // caracteres especiales
			return "cd";	
		} else if (token.matches("\\{")) { // caracteres especiales
			return "li";	
		} else if (token.matches("\\}")) { // caracteres especiales
			return "ld";	
		} else if (token.matches("[a-z]([0-9]|[a-z]|[A-Z]|_)*")) { // identificadores
			return "ident";
		} else if (token.matches("(-)?[0-9]+(\\.)[0-9]+")) { // float
			return "float";
		} else if (token.matches("(-)?[0-9]+")) { // int
			return "int";
		}
		error = true;
		return "errorlex";
	}

}
