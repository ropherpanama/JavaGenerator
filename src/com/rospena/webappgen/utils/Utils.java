package com.rospena.webappgen.utils;

/**
 * @author rospena
 */
public class Utils {
	private static String className;

	private static String getClassName() {
		return className;
	}

	private static void setClassName(String className) {
		Utils.className = className;
	}

	/**
	 * Este metodo convierte al patron de nombres de Java clases, variables,
	 * metodos, etc.
	 * @param input cadena a convertir
	 * @param var true para nombrar metodos y variables, false para nombrar clases
	 * @return nombre java standard
	 */
	public static String javaNaming(StringBuilder input, boolean var) {
		convertName(input, input.length(), var);
		return Utils.getClassName();
	}

	public static void out(String a) {
		System.out.println(a);
	}

	/**
	 * Este metodo transforma cadenas de tipo nombre_tabla en NombreTabla, se
	 * usa para armar nombres de clase a partir de tablas de base de datos
	 * 
	 * @param str
	 *            cadena a transformar
	 * @param n
	 *            longitud de la cadena
	 * @param var
	 *            indica si el nombre a generar es de variable, si es true la
	 *            primera letra sera minuscula.
	 */
	private static void convertName(StringBuilder str, int n, boolean var) {
		if (n > 0) {
			int index = str.indexOf("_");
			char letter = str.charAt(index + 1);
			str.setCharAt(index + 1, Character.toUpperCase(letter));

			if (index > -1)
				str.deleteCharAt(index);

			convertName(str, n - 1, var);

			if (var)
				str.setCharAt(0, Character.toLowerCase(str.charAt(0)));

			setClassName(str.toString());
		}
	}
}

//class Prueba {
//	public static void main(String[] args) {
//		StringBuilder str = new StringBuilder("imp_cargos");
//		Utils.out("========> " + Utils.javaNaming(str));
//	}
//}