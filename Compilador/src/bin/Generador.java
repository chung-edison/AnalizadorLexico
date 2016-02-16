package bin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.Nodo;

public class Generador {
	
	private ArrayList<String[]> vars;
	private int i; //número de variable (registro)
	private int l; //número de etiqueta
	private int st; //para calcular la direccion de las variables globales
	private int heap; //para calcular la direccion de las variables
	
	public Generador(){
		super();
		vars = new ArrayList<String[]>();
		i = 1;
		l = 1;
		st = 0;
		heap = 0;
	}
	
	//generador de código ensamblador
	public void generar(ArrayList<Nodo> arboles){
		String salida = "";
		for(Nodo arbol:arboles){
			if (arbol.getInfo().matches("#VARLOCAL")) {
				salida += registrar(arbol);
			} else
			if (arbol.getInfo().matches("#VARGLOBAL")) {
				salida += registrarVect(arbol);
			}
			salida += traducir(arbol);
		}
		
		Matcher m = Pattern.compile("\r\n").matcher(salida);
		int lineas = 0;
		while (m.find())
		{
		    lineas++;
		}
		lineas *= 4; //suponiendo que cada linea generada ocupa 4 bytes en memoria
		lineas += 8; //añadimos 8 bytes para las dos siguientes lineas
		//fp es el frame pointer o inicio del bloque de memoria donde está el programa
		//tp es el inicio de la parte de las variables globales (static), su valor final es el inicio del stack
		//hp es el final del bloque, donde inicia el heap
		salida = "addI fp, " + lineas + " => tp\r\n" 
				+ "addI fp, " + (lineas * 3) + " => hp\r\n" + salida;
		//asumimos que el tamaño del stack y del heap es el mismo que el del código
		//memoria total ocupada por el programa es 3 * memoria ocupada por el código
		
		//System.out.println(expandir(arbol));
		
		//guardar el código generado en un archivo.asm
		BufferedWriter output = null;	
		
		try {			
			File outputFile = new File("assembly.asm");
			output = new BufferedWriter(new FileWriter(outputFile));
			output.write(salida);		
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
	
	//aumento un registro correspondiente a un vector global
	public String registrarVect(Nodo arbol) {
		String[] a = { arbol.getHijos().get(0).getHijos().get(2).getDato(), "r" + i };
		vars.add(a);
		i++;
		String tipo = arbol.getHijos().get(0).getHijos().get(2).getInfo();
		int t = 0;
		if (tipo.matches("int")) {
			t = 2;
		} else if (tipo.matches("float")) {
			t = 4;
		} else if (tipo.matches("bool")) {
			t = 1;
		} else if (tipo.matches("char")) {
			t = 1;
		}
		t *= Integer.parseInt(arbol.getHijos().get(2).getDato());
		tipo = "addI tp, " + st + " => " + a[1] + "\r\n";
		st += t;
		return tipo;

	}
	
	//aumenta un registro correspondiente a la variable a la lista
	public String registrar(Nodo arbol) {
		String[] a = { arbol.getHijos().get(2).getDato(), "r" + i };
		vars.add(a);
		i++;
		String tipo = arbol.getHijos().get(0).getDato();
		int t = 0;
		if (tipo.matches("int")) {
			t = 2;
		} else if (tipo.matches("float")) {
			t = 4;
		} else if (tipo.matches("bool")) {
			t = 1;
		} else if (tipo.matches("char")) {
			t = 1;
		}
		if (arbol.getPadre() == null){
			tipo = "addI tp, " + st + " => " + a[1] + "\r\n";
			st += t;
			return tipo;
		}
		heap += t;
		return "subI hp, " + heap + " => " + a[1] + "\r\n";
	}
	
	//traducir segun lo que encuentre en el arbol respectivo
	public String traducir(Nodo arbol){
		String salida = "";
		String info = arbol.getInfo();
		if (info.matches("#FUNC")) salida += arbol.getHijos().get(0).getHijos().get(2).getDato() + "_entry:\r\n";
		if (info.matches("#VARLOCAL") && arbol.getPadre() != null && !arbol.getPadre().getInfo().matches("#FUNC")) {
			return registrar(arbol);
		}
		if (info.matches("#COM") && arbol.getHijos().get(0).getInfo().matches("#IDENT")) {
			String[] expandido = expandir(arbol).split("\\s+=\\s+");
			String tipo = arbol.getHijos().get(2).getInfo();
			if (tipo.matches("#VAR")) {
				salida = "load " + expandido[1].trim() + " => a0\r\n";
			} else if (tipo.matches("#IDENT")) {
				salida = "loadI " + expandido[1].trim() + " => a0\r\n";
			} else if (tipo.matches("#ARIT")) {
				salida = arit(expandido[1]);
			} else if (tipo.matches("#LOGIC")) {
				salida = logic(expandido[1]);
			}
			salida += "store a0 => " + expandido[0] + "\r\n";
			return salida;
		}
		if (info.matches("#IF")) {
			if (arbol.getHijos().get(0).getInfo().matches("#IF")) {			
				salida += traducir(arbol.getHijos().get(0));
				salida += traducir(arbol.getHijos().get(2));
			} else {
				salida += logic(expandir(arbol.getHijos().get(2)));
				salida += "cbr a0 => l" + l + ", l" + (l+1) + "\r\nl" + l + ":\r\n"; //a0 es booleano, salta a l1 (if) o l2 (else)
				l++;
				salida += traducir(arbol.getHijos().get(5));
				if (arbol.getPadre().getInfo().matches("#IF")) {
					salida += "jumpI l" + (l + 1) + "\r\n"; //si termina el primer bloque (if) salta despues del else
				}
			}
			salida += "l" + l + ":\r\n";
			l++;
			return salida;
		}
		if (info.matches("#WHILE")){
			salida += "l" + l + ":\r\n";
			l++;
			salida += logic(expandir(arbol.getHijos().get(2)));
			salida += "cbr a0 => l" + l + ", l" + (l+1) + "\r\nl" + l + ":\r\n"; //a0 es booleano, salta a l1 (dentro del while) o l2 (fuera del while)
			l++;
			salida += traducir(arbol.getHijos().get(5));
			salida += "jumpI l" + (l - 2) + "\r\n";
			salida += "l" + l + ":\r\n";
			l++;
			return salida;
		}
		if(info.matches("#DO")){
			salida += "l" + l + ":\r\n";
			l++;
			salida += traducir(arbol.getHijos().get(1));
			salida += logic(expandir(arbol.getHijos().get(4)));
			salida += "cbr a0 => l" + (l - 1) + ", l" + l + "\r\nl" + l + ":\r\n"; //a0 es booleano, salta a l1 (inicio del do) o l2 (despues del while)
			l++;
			return salida;
		}
		if (!arbol.getHijos().isEmpty()) {
			for (Nodo nodo : arbol.getHijos()) {
				salida += traducir(nodo);
			}
		}
		return salida;
	}
	
	//buscar el registro asignado a la variable x
	public String var(String x){
		for(String[] v:vars){
			if(x.matches(v[0])) return v[1];
		}
		return "";
	}
	
	//retorna todos los terminales del arbol como un string, reemplazando las variables por sus registros
	public String expandir(Nodo nodo){
		String expand = "";
		if(nodo.getHijos().isEmpty()) {
			if(nodo.getInfo().matches("ident")){
				return var(nodo.getDato());
			}
			return nodo.getDato();
		}
		for(Nodo n:nodo.getHijos()){
			expand += expandir(n) + " ";
		}
		return expand;
	}
	
	//traduccion de operaciones aritmeticas
	public String arit(String cod) {
		String result = "";
		Stack<String> aux = new Stack<String>();
		String[] a = cod.split("\\s+");
		for (String s : a) {
			if (!s.equals("")) {
				try {
					if (s.matches("r.")){
						result += "load " + s + " => a0\r\n"; // acc <- x
					}
					else {
						Integer.parseInt(s);
						result += "loadI " + s + " => a0\r\n"; // acc <- x
					}
					if (!aux.isEmpty()) {
						if (aux.peek().matches("-|\\*|\\/")) {
							result += "loadAI sp, 4 => t1\r\n" //acc + top of stack
									+ aux.pop() + " a0, t1 => a0\r\n"
									+ "addi sp, 4 => sp\r\n"; //pop
						}
					}
				} catch (NumberFormatException e) {
					if (s.matches("\\(")) {
						aux.push(s);
					} else if (s.matches("\\)")) {
						while (!aux.peek().matches("\\(")) {
							result += "loadAI sp, 4 => t1\r\n" //acc + top of stack
									+ aux.pop() + " a0, t1 => a0\r\n"
									+ "addi sp, 4 => sp\r\n"; //pop
						}
						aux.pop();
					} else {
						result += "store a0 => sp\r\n"
								+ "subi sp, 4 => sp\r\n"; //push acc
						aux.push(s);
					}
				}
			}
		}
		while (!aux.empty()) {
			result += "loadAI sp, 4 => t1\r\n"
					+ aux.pop() + " a0, t1 => a0\r\n"
					+ "addi sp, 4 => sp\r\n";
		}
		result = result.replaceAll("\\+", "add");
		result = result.replaceAll("-", "sub");
		result = result.replaceAll("\\*", "mult");
		result = result.replaceAll("\\/", "div");
		return result;
	}
	
	//traducir operacion de comparacion logica
	public String logic(String cod) {
		String result = "";
		String[] a = cod.split("\\s+");
		if (a[0].matches("r.")) {
			result += "load " + a[0] + " => t1\r\n";
		} else {
			result += "loadI " + a[0] + " => t1\r\n";
		}
		if (a[2].matches("r.")) {
			result += "load " + a[2] + " => t2\r\n";
		} else {
			result += "loadI " + a[2] + " => t2\r\n";
		}
		
		if(a[1].matches("<")){
			result += "cmp_LT t1, t2 => a0\r\n";
		}else if(a[1].matches("<=")){
			result += "cmp_LE t1, t2 => a0\r\n";
		}else if(a[1].matches("==")){
			result += "cmp_EQ t1, t2 => a0\r\n";
		}else if(a[1].matches(">=")){
			result += "cmp_GE t1, t2 => a0\r\n";
		}else if(a[1].matches(">")){
			result += "cmp_GT t1, t2 => a0\r\n";
		}else if(a[1].matches("!=")){
			result += "cmp_NE t1, t2 => a0\r\n";
		}else if(a[1].matches("&&")){
			result += "and t1, t2 => a0\r\n";
		}else if(a[1].matches("\\|\\|")){
			result += "or t1, t2 => a0\r\n";
		}
		return result;
	}
}
