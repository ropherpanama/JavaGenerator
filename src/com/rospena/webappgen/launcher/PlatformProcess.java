package com.rospena.webappgen.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.rospena.webappgen.beans.DBConfigBean;
import com.rospena.webappgen.configuration.LoaderConfigs;
import com.rospena.webappgen.database.Connector;
import com.rospena.webappgen.database.DatabaseProcess;
import com.rospena.webappgen.utils.SystemUtils;
import com.rospena.webappgen.utils.Utils;

/**
 * @author rospena
 */
public class PlatformProcess {

	private static boolean stayWait = true;

	public static boolean isStayWait() {
		return stayWait;
	}

	public static void setStayWait(boolean stayWait) {
		PlatformProcess.stayWait = stayWait;
	}

	public static void showMainMenu() throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("******************************************************************");
		System.out.println("*                           MENU                                 *");
		System.out.println("******************************************************************");
		System.out.println("Escriba el número de la tarea que desea realizar");
		System.out.println("1. Generar Beans a partir de base de datos");
		System.out.println("2. Generar capa DAO e Implementaciones");
		System.out.println("3. Generar capa de Servicios e Implementaciones");
		System.out.println("0. Salir");

		while (PlatformProcess.isStayWait()) {
			System.out.print("Ingrese su opción: ");
			String input = sc.nextLine();

			if (input.equals("0")) {
				PlatformProcess.setStayWait(false);
			} else if (input.equals("1")) {
				PlatformProcess.generateBeansFromDBMenu();
			} else
				System.out.println("Opción no válida");
		}

		sc.close();
		System.out.print("Programa terminado");
		System.exit(0);
	}

	public static void generateBeansFromDBMenu() throws Exception {
		SystemUtils.clrscr();
		Scanner sc = new Scanner(System.in);
		System.out.println("Generar Beans a partir de Base de Datos");
		System.out.println("1. Listar bases de datos disponibles");
		System.out.println("2. Crear nueva conexion");
		System.out.println("3. Volver al menú anterior");
		System.out.println("0. Salir");

		while (PlatformProcess.isStayWait()) {
			System.out.print("Ingrese su opción: ");
			String input = sc.next();

			if (input.equals("0")) {
				PlatformProcess.setStayWait(false);
			} else if (input.equals("1")) {
				LinkedList<DBConfigBean> p = LoaderConfigs.loadAvaiableDatabases();

				if (p != null) {
					System.out.println("Conexiones disponibles");
					selectDatabase(p);
				}
			} else if (input.equals("3")) {
				showMainMenu();
			} else
				System.out.println("Opción no válida");
		}
		sc.close();
		System.out.print("Programa terminado");
		System.exit(0);
	}

	/**
	 * Realiza la conexion a la base de datos seleccionada
	 * 
	 * @param conections
	 *            conexiones disponibles
	 */
	private static void selectDatabase(LinkedList<DBConfigBean> conections) {
		try {
			HashMap<Integer, DBConfigBean> options = new HashMap<Integer, DBConfigBean>();
			int selectedConfig = -1;
			int maxOption = conections.size() + 1;
			Scanner sc = new Scanner(System.in);

			//Generacion de menu en base a las bdd existentes
			for (int i = 0; i < conections.size(); i++) {
				options.put(i + 1, conections.get(i));
				System.out.println(i + 1 + ". " + conections.get(i).getP().getProperty("connectionName"));
			}

			System.out.println(maxOption + ". Volver al menú anterior");
			System.out.println("0. Salir");
			//Generacion de menu en base a las bdd existentes

			while (PlatformProcess.isStayWait()) {
				System.out.print("Ingrese su opción: ");
				String input = sc.nextLine();
				selectedConfig = Integer.valueOf(input);

				if (input.equals("0"))
					PlatformProcess.setStayWait(false);
				else if (input.equals(String.valueOf(maxOption))) {
					PlatformProcess.generateBeansFromDBMenu();
				} else if (0 < selectedConfig && selectedConfig < maxOption) {
					List<String> tablasDisponibles = new ArrayList<String>();
					DBConfigBean selected = options.get(Integer.valueOf(input));
					System.out.println("Base de datos seleccionada: " + selected.getPropertiesName());
					Connector connection = new Connector(selected.getP());
					if (connection.getConnection() != null) {
						tablasDisponibles = connection.obtenerTablasUsuario();
						if (tablasDisponibles.size() > 0) {
							DatabaseProcess.selectTables(tablasDisponibles, null, connection);
//							PlatformProcess.showMainMenu();
							PlatformProcess.setStayWait(false);
						} else
							Utils.out("No existen tablas para el usuario " + selected.getP().getProperty("username"));
					}

					Utils.out("");
				} else
					System.out.println("Opción no válida");
			}
			sc.close();
			System.out.print("Programa terminado");
			System.exit(0);
		} catch (Exception e) {
		}
	}
}
