/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rospena.webappgen.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import com.rospena.webappgen.beans.DBConfigBean;

/**
 *
 * @author rospena
 */
public class LoaderConfigs {
	public static LinkedList<DBConfigBean> loadAvaiableDatabases() {
		LinkedList<DBConfigBean> list = new LinkedList<DBConfigBean>();
		try {
			File f = new File("resources/");
			String[] files = f.list();

			if (files.length > 0) {
				for(String current: files) {
					Properties prop = new Properties();
					InputStream is = new FileInputStream(f.getPath() + "/" + current);
					prop.load(is);
					
					list.add(new DBConfigBean(prop, f.getPath() + "/" + current)); 
				}
				return list;
			} else {
				System.out.println("No existen conexiones configuradas");
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No existen conexiones configuradas, error de sistema: " + e);
			return null;
		}
	}
}
