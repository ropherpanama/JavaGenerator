package com.rospena.webappgen.database;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import com.rospena.webappgen.tasks.GenerateDBBeansTask;
import com.rospena.webappgen.utils.Utils;

public class DatabaseProcess {

	private static boolean stayWait = true;
	private static boolean generateDaoLayer = false;
	private static boolean generateServiceLayer = false;
	private static boolean anotateEntity = false;

	public static boolean isStayWait() {
		return stayWait;
	}

	public static void setStayWait(boolean stayWait) {
		DatabaseProcess.stayWait = stayWait;
	}

	public static boolean isGenerateDaoLayer() {
		return generateDaoLayer;
	}

	public static void setGenerateDaoLayer(boolean generateDaoLayer) {
		DatabaseProcess.generateDaoLayer = generateDaoLayer;
	}

	public static boolean isGenerateServiceLayer() {
		return generateServiceLayer;
	}

	public static void setGenerateServiceLayer(boolean generateServiceLayer) {
		DatabaseProcess.generateServiceLayer = generateServiceLayer;
	}

	public static boolean isAnotateEntity() {
		return anotateEntity;
	}

	public static void setAnotateEntity(boolean anotateEntity) {
		DatabaseProcess.anotateEntity = anotateEntity;
	}

	public static void selectTables(List<String> tablas, List<Integer> selecteds, Connector c) {
		Scanner sc = new Scanner(System.in);

		int counter = 1;
		for (String s : tablas) {
			System.out.format("%-3s%-30s", counter, s);
			if (counter % 4 == 0) {
				Utils.out("");
			}
			counter++;
		}

		Utils.out(
				"\nEscriba el número de la tabla que desea generar y presiones ENTER, puede seguir ingresando más tablas de esta forma, presione q(Q) para procesarlas: ");

		if (selecteds == null)
			selecteds = new LinkedList<Integer>();
		else
			Utils.out("Actuales ==> " + selecteds);

		while (isStayWait()) {
			String input;
			try {
				input = sc.nextLine();
				
				if (input.toLowerCase().equals("q")) {
					Utils.out("Desea añadir anotaciones JPA? s/n");
					String answer = sc.nextLine();
					if(answer.toLowerCase().equals("s"))
						DatabaseProcess.setAnotateEntity(true);
					
					Utils.out("Desea generar la capa DAO para estos beans? s/n");
					answer = sc.nextLine();
					if(answer.toLowerCase().equals("s"))
						DatabaseProcess.setGenerateDaoLayer(true);
					
					Utils.out("Desea generar la capa de servicios para estos beans? s/n");
					answer = sc.nextLine();
					if(answer.toLowerCase().equals("s"))
						DatabaseProcess.setGenerateServiceLayer(true);
					
					Utils.out("Procesando tablas ...");
					
					for (Integer i : selecteds) {
						GenerateDBBeansTask g = new GenerateDBBeansTask(c);
						g.cargarEspecificaciones(tablas.get(i), DatabaseProcess.isAnotateEntity());
						
						if(DatabaseProcess.generateDaoLayer) {
							g.generateDAO(tablas.get(i));
							g.generateDAOImplement(tablas.get(i));  
						}
						
						g = null;
					}
					Utils.out("Los fuentes han sido generados");
					sc.close();
					DatabaseProcess.setStayWait(false);
				}else {
					if (Integer.valueOf(input) <= 0 || Integer.valueOf(input) > tablas.size())
						Utils.out("No existe la tabla " + input);
					else {
						selecteds.add(Integer.valueOf(input) - 1);
						Utils.out("==> " + selecteds);
					}
				}
			} catch (java.lang.NumberFormatException i) {
				Utils.out("No se admiten letras, escriba solo números :");
				selectTables(tablas, selecteds, c);
			}
		}
	}
}
