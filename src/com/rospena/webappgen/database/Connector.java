package com.rospena.webappgen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Connector {
	private Connection connection = null;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	private String user, pass, url, driver;
	private StringBuilder status = new StringBuilder("Database Connection => ");
	private Properties p;

	/**
	 * Constructor de la clase Connector
	 */
	public Connector(Properties p) {
		this.p = p;
	}

	public void cargarDatosConexion() {
		try {
			user = p.getProperty("username");
			pass = p.getProperty("password");
			
			//Bifurcacion de tipos de base de datos para armar cadena de conexion
			if (p.getProperty("driverClassName").toLowerCase().contains("mysql"))//MySQL
				url = p.getProperty("url") + p.getProperty("dbSid");
			if (p.getProperty("driverClassName").toLowerCase().contains("oracle"))//Oracle
				url = p.getProperty("url") + ":" + p.getProperty("dbSid");
			
		} catch (Exception e) {
			// System.err.println("<< CadenaConexion : " + e.getMessage());
			status.append("Error al obtener los datos de conexión " + e.getMessage());
			user = "";
			pass = "";
			url = "";
		}
	}

	/**
	 * Realiza la conexion a la base de datos
	 * 
	 * @return connection Objeto con la conexion
	 */
	public Connection makeConnection() {
		try {
			cargarDatosConexion();
			driver = p.getProperty("driverClassName");
			Class.forName(driver);
			System.out.println(user + " - " + pass + " - " + url);
			connection = DriverManager.getConnection(url, user, pass);
			if (connection != null) {
				status.append("conectado a ").append(p.getProperty("connectionName"));  
				return connection;
			} else {
				status.append("Error: No se pudo establecer la conexion");
				return null;
			}
		} catch (Exception e) {
			status.append("Error con la base de datos.\n").append(e.getMessage())
					.append("\nAsegúrese de que los parámetros de la conexión están correctos.");
			return null;
		}
	}

	public String getStatus() {
		return status.toString();
	}

	/**
	 * Retorna la conexion establecida
	 * 
	 * @return connection
	 */
	public Connection getConnection() {
		if (connection != null)
			return connection;
		else
			return makeConnection();
	}

	/**
	 * Realiza el touch a la tabla pasada como argumento y carga el objeto
	 * resultSet que contiene las propiedades de la misma.
	 * 
	 * @param table_name
	 *            Nombre de la tabla consultada
	 */
	public int touchTable(String table_name) {
		try {
			pst = this.getConnection().prepareStatement("Select * from " + table_name + " where 1 = 0");
			rs = pst.executeQuery();
			return 1;
		} catch (Exception e) {
			System.err.println("<< ¿Tabla " + table_name + "? ¿Es correcto este nombre? " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna el objeto resultSet con la informacion de la tabla consultada
	 * 
	 * @return
	 */
	public ResultSet getResulset() {
		if (rs != null)
			return rs;
		else
			return null;
	}

	/**
	 * Este método retorna la lista de tablas que pertenecen al usuario
	 * conectado a la base de datos
	 * 
	 * @return List con todas las tablas del usuario
	 */
	public List<String> obtenerTablasUsuario() {
		try {
			PreparedStatement ps = null;
			if(p.getProperty("dbClient").toLowerCase().equals("oracle")) 
				ps = this.getConnection().prepareStatement("SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER ='" + user.toUpperCase() + "'");
			else if (p.getProperty("dbClient").toLowerCase().equals("mysql"))
				ps = this.getConnection().prepareStatement("SHOW TABLES");
			
			ResultSet r = ps.executeQuery();
			ResultSetMetaData rmd = r.getMetaData();
			int columnCount = rmd.getColumnCount();
			List<String> retorno = new ArrayList<String>();

			while (r.next()) {
				for (int i = 0; i < columnCount; i++) {
					retorno.add(r.getString(i + 1));
					// System.out.println("USER_TABLE : " + r.getString(i + 1));
				}
			}
			return retorno;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
