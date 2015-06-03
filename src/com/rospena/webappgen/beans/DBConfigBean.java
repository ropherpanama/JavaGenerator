package com.rospena.webappgen.beans;

import java.util.Properties;

/**
 * @author rospena
 */
public class DBConfigBean {
	private Properties p;
	private String propertiesName;

	public DBConfigBean(Properties p, String propertiesName) {
		super();
		this.p = p;
		this.propertiesName = propertiesName;
	}

	public Properties getP() {
		return p;
	}

	public void setP(Properties p) {
		this.p = p;
	}

	public String getPropertiesName() {
		return propertiesName;
	}

	public void setPropertiesName(String propertiesName) {
		this.propertiesName = propertiesName;
	}
}
