package com.rospena.webappgen.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.rospena.webappgen.database.Connector;
import com.rospena.webappgen.utils.Utils;

/**
 * @author rospena
 */
public class GenerateDBBeansTask {
	private TableInfo t = null;
	private List<String> variables = new ArrayList<String>();
	private List<String> referencia = new ArrayList<String>();
	private List<Integer> precision = new ArrayList<Integer>();
	private Properties props = new Properties();
	private String head;
	private String declare;
	private String campos;
	private String getter;
	private String setter;
	private String foot;
	private boolean import_date = false;
	private File dir = new File("generated");
	private String path_end = "";

	public GenerateDBBeansTask(Connector c) {
		t = new TableInfo(c);
	}

	/**
	 * Carga las lineas del fichero .java que se generara
	 * 
	 * @param table_name
	 *            Nombre de la tabla (y del futuro fichero .java)
	 */
	public void cargarEspecificaciones(String table_name, boolean isEntity) {
		try {
			table_name = table_name.toLowerCase();
			String temp1 = "";
			String temp2 = "";
			String temp3 = "";
			
			if(isEntity)
				props.load(GenerateDBBeansTask.class.getClassLoader().getResourceAsStream("entity-template.properties"));
			else
				props.load(GenerateDBBeansTask.class.getClassLoader().getResourceAsStream("class-template.properties"));
			
			if (t.cargarDatosTabla(table_name) > 0) {
				variables = t.getColumnNames();
				referencia = t.getColumnJavaClass();
				precision = t.getColumnPrecisions();
				head = props.getProperty("class_head");
				declare = props.getProperty("class_declare");
				campos = props.getProperty("class_fields");
				getter = props.getProperty("class_getters");
				setter = props.getProperty("class_setters");
				foot = props.getProperty("class_end");
				// ************************************************************//
				// CONSTRUCCION DEL ARCHIVO
				// ************************************************************//
				if (!dir.exists())
					dir.mkdirs();

				if(isEntity)
					declare = declare.replace("#table_name", table_name);
				
				table_name = Utils.javaNaming(new StringBuilder(table_name), false);
				
				File dot_java = new File(dir.getPath() + "/" + table_name + ".java");

				if (!dot_java.exists())
					dot_java.createNewFile();

				FileWriter fw = new FileWriter(dot_java.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);

				declare = declare.replace("#class_name", table_name);

				for (int b = 0; b < referencia.size(); b++) {
					// Conversion de tipos Oracle a tipos Java 
					if (referencia.get(b).contains("String"))
						referencia.set(b, "String");
					else if (referencia.get(b).contains("BigDecimal") && precision.get(b) == 0)
						referencia.set(b, "int");
					else if (referencia.get(b).contains("BigDecimal") && precision.get(b) > 0)
						referencia.set(b, "double");
					else if (referencia.get(b).contains("Double"))
						referencia.set(b, "double");
					else if (referencia.get(b).contains("Date") || referencia.get(b).contains("Timestamp")) {
						referencia.set(b, "Date");
						import_date = true;
					}
				} // Conversion de tipos Oracle a tipos Java

				for (int a = 0; a < variables.size(); a++) {
					String varName = Utils.javaNaming(new StringBuilder(variables.get(a).toLowerCase()), true);
					
					if(isEntity) {
						if(referencia.get(a).equals("Date")) {
							campos = "\t@Temporal(TemporalType.TIMESTAMP)\n" + campos;
						}else
							campos = campos.replace("\t@Temporal(TemporalType.TIMESTAMP)\n", "");
					}
					
					temp1 = temp1 + campos.replace("#field_name", varName) + "\n";				
					temp1 = temp1.replace("#type", referencia.get(a));
					temp1 = temp1.replace("#column_id", variables.get(a).toLowerCase());
					
					String methodName = Utils.javaNaming(new StringBuilder(varName), false);
					
					temp2 = temp2 + getter.replace("#field_name", varName) + "\n";
					temp2 = temp2.replace("#method_name", methodName) + "\n";
					temp2 = temp2.replace("#type", referencia.get(a));
					
					temp3 = temp3 + setter.replace("#field_name", varName) + "\n";
					temp3 = temp3.replace("#method_name", methodName) + "\n";
					temp3 = temp3.replace("#type", referencia.get(a));
				}

				if (import_date) head = head + "import java.util.Date;\n\n";

				variables.clear();
				referencia.clear();
				precision.clear();

				bw.write(head);
				bw.write(declare);
				bw.newLine();
				bw.write(temp1);
				bw.newLine();
				if(isEntity)
					bw.write(props.getProperty("constructor").replace("#class_name", table_name) + "\n");
				bw.newLine();
				bw.write(temp2);
				bw.write(temp3);
				bw.write(foot);
				bw.close();
				path_end = dir.getCanonicalPath();
				
				Utils.out(">> " + dot_java.getName() + " Hecho!");
				// ************************************************************//
				// FIN DE CONSTRUCCION DEL ARCHIVO
				// ************************************************************//
			} else {
				System.err.println("<< Sin informacion para " + table_name);
			}
		} catch (Exception e) {
			System.err.println("<< No se especificaron tablas : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getPath_end() {
		return path_end;
	}
	
	public void generateDAO(String table_name) {
		try {
			String beanName = Utils.javaNaming(new StringBuilder(table_name), false);
			String interfaceName = beanName + "DAO";
			File f = new File(dir.getPath() + "/" + interfaceName + ".java");
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("package com.generated.dao;\n\n");
			bw.write("import com.generated.beans." + beanName + ";\n");
			bw.write("import java.util.List;\n\n"); 
			bw.write("public interface " + interfaceName + " {\n");
			bw.write("\tpublic " + beanName + " findById(int id);\n");
			bw.write("\tpublic List<" + beanName + "> findAll();\n");
			bw.write("\tpublic void add(" + beanName + " "+ beanName.substring(0, 1).toLowerCase() + ");\n");
			bw.write("\tpublic void update(" + beanName + " "+ beanName.substring(0, 1).toLowerCase() + ");\n");
			bw.write("\tpublic void remove(Integer id);\n"); 
			bw.write("}");
			bw.close();
			path_end = dir.getCanonicalPath();
			Utils.out(">> " + f.getName() + " Hecho!");
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void generateDAOImplement(String table_name) {
		try {
			String beanName = Utils.javaNaming(new StringBuilder(table_name), false);
			String interfaceName = beanName + "DAOImpl";
			File f = new File(dir.getPath() + "/" + interfaceName + ".java");
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("package com.generated.dao;\n\n");
			bw.write("import com.generated.beans." + beanName + ";\n");
			bw.write("import java.util.List;\n"); 
			bw.write("import org.hibernate.Session;\n");
			bw.write("import org.hibernate.SessionFactory;\n");
			bw.write("import org.springframework.beans.factory.annotation.Autowired;\n");
			bw.write("import org.springframework.stereotype.Repository;\n\n");
			bw.write("@Repository\n"); 
			bw.write("public class " + interfaceName + " implements " + beanName + "DAO {\n");
			bw.write("\t@Autowired\n\tprivate SessionFactory sessionFactory;\n\n");
			bw.write("\tprivate Session getCurrentSession() {\n\t\treturn sessionFactory.getCurrentSession();\n\t}\n\n");
			bw.write("\t@Override\n");
			bw.write("\tpublic " + beanName + " findById(int id) {\n");
			bw.write("\t\t" + beanName + " o = ("+ beanName +") getCurrentSession().load(" + beanName + ".class, id);\n");
			bw.write("\t\treturn o;\n");
			bw.write("\t}\n\n");
			bw.write("\t@Override\n");
			bw.write("\tpublic List<" + beanName + "> findAll() {\n");
			bw.write("\t\treturn sessionFactory.getCurrentSession().createQuery(\"from " + beanName + "\")\n");
			bw.write("\t\t\t.list();\n");
			bw.write("\t}\n\n");
			bw.write("\t@Override\n");
			bw.write("\tpublic void add(" + beanName + " o) {\n");
			bw.write("\t\tsessionFactory.getCurrentSession().save(o);\n");
			bw.write("\t}\n\n");
			bw.write("\t@Override\n");
			bw.write("\tpublic void update(" + beanName + " o) {\n");
			bw.write("\t\tsessionFactory.getCurrentSession().update(o);\n");
			bw.write("\t}\n\n");
			bw.write("\t@Override\n");
			bw.write("\tpublic void remove(Integer id) {\n");
			bw.write("\t\t" + beanName + " l = (" + beanName + ") sessionFactory.getCurrentSession().load(" + beanName + ".class, id);\n");
			bw.write("\t\tif (null != l) {\n");
			bw.write("\t\tsessionFactory.getCurrentSession().delete(l);\n");
			bw.write("\t}\n");
			bw.write("}");
			bw.close();
			path_end = dir.getCanonicalPath();
			Utils.out(">> " + f.getName() + " Hecho!");
		}catch(Exception e){e.printStackTrace();}
	}
}
