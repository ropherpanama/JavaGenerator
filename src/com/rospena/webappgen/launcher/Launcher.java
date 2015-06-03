/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rospena.webappgen.launcher;

import com.rospena.webappgen.utils.SystemUtils;

/**
 *
 * @author rospena
 */
public class Launcher {
	public static void main(String[] args) {
		try {
			System.out.println("OS : " + SystemUtils.OSVersion());
			System.out.println("Java version : " + SystemUtils.jVMVersion());
			System.out.println("------------------------ Bienvenido ------------------------");
			PlatformProcess.showMainMenu();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
}
