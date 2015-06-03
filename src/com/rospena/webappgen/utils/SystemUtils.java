package com.rospena.webappgen.utils;

import java.util.Locale;

public class SystemUtils {
	public static String OSVersion() {
		return System.getProperty("os.name");
	}

	public static String jVMVersion() {
		return System.getProperty("java.version");
	}

	public static boolean isWindowsOS() {
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows"))
			return true;
		else
			return false;
	}

	public static boolean isUnixOS() {
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("unix"))
			return true;
		else
			return false;
	}

	public static boolean isLinuxOS() {
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("linux"))
			return true;
		else
			return false;
	}

	public static void clrscr() throws Exception {
		Process p;
		if (isLinuxOS() || isUnixOS())
			p = Runtime.getRuntime().exec("clear");
		else if (isWindowsOS())
			p = Runtime.getRuntime().exec("cmd.exe /C cls");
	}
}
