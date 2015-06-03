package com.rospena.webappgen.tasks;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.rospena.webappgen.database.Connector;

/**
 * Esta clase cargara todas las propiedades de la tabla requerida (nombre,
 * campos, tipo de dato de los campos, etc)
 * 
 * @author rospena
 */
public class TableInfo {
	private Connector c = null;
	private ResultSetMetaData rmd = null;
	private int columnCount = 0;
	private List<String> columnNames = new ArrayList<String>();
	private List<String> columnTypes = new ArrayList<String>();
	private List<Integer> columnPrecisions = new ArrayList<Integer>();
	private List<String> columnJavaClass = new ArrayList<String>();

	public TableInfo(Connector c) {
		this.c = c;
	}

	/**
	 * Carga los datos de la tabla consultada
	 * 
	 * @param table_name
	 *            Nombre de la tabla que se desea consultar
	 * @return Mayor que 0 si OK, menor que 0 si ERROR
	 */
	public int cargarDatosTabla(String table_name) {
		try {
			if (c.touchTable(table_name) > 0) {
				rmd = c.getResulset().getMetaData();
				columnCount = rmd.getColumnCount();

				for (int i = 0; i < columnCount; i++) {
					columnNames.add(rmd.getColumnName((i + 1)));
					columnTypes.add(rmd.getColumnTypeName((i + 1)));
					columnJavaClass.add(rmd.getColumnClassName((i + 1)));
					columnPrecisions.add(rmd.getScale((i + 1)));
				}
				return 1;
			} else {
				return -1;
			}
		} catch (Exception e) {
			System.out.println("<< Error al cargar los datos de la tabla " + table_name + "\n" + e.getMessage());
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Retorna la lista de los tipos de dato java que corresponden a los campos
	 * de la tabla
	 * 
	 * @return
	 */
	public List<String> getColumnJavaClass() {
		return columnJavaClass;
	}

	/**
	 * Retorna una lista con el nombre de los campos de la tabla
	 * 
	 * @return
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * Retorna una lista con la precision de los campos (si son doubles)
	 * 
	 * @return
	 */
	public List<Integer> getColumnPrecisions() {
		return columnPrecisions;
	}

	/**
	 * Retorna una lista con el tipo de datos del campo en basado en la BDD
	 * 
	 * @return
	 */
	public List<String> getColumnTypes() {
		return columnTypes;
	}
}
