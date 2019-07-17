package com.alex_zaitsev.adg.util;

import java.io.File;

public class CodeUtils {

	public static boolean isClassGenerated(String className) {
		return className != null && className.contains("$$");
	}

	public static boolean isClassInner(String className) {
		return className != null && className.contains("$") && !isClassAnonymous(className) && !isClassGenerated(className);
	}

	public static String getOuterClass(String className) {
		return className.substring(0, className.lastIndexOf("$"));
	}

	public static boolean isClassAnonymous(String className) {
		return className != null && className.contains("$")
				&& StringUtils.isNumber(className.substring(className.lastIndexOf("$") + 1, className.length()));
	}

	public static String getAnonymousNearestOuter(String className) {
		String[] classes = className.split("\\$");
		for (int i = 0; i < classes.length; i++) {
			if (StringUtils.isNumber(classes[i])) {
				String anonHolder = "";
				for (int j = 0; j < i; j++) {
					anonHolder += classes[j] + (j == i - 1 ? "" : "$");
				}
				return anonHolder;
			}
		}
		return null;
	}

	public static int getEndGenericIndex(String line, int startGenericIndex) {
		int endIndex = line.indexOf(">", startGenericIndex);
		for (int i = endIndex + 2; i < line.length(); i += 2) {
			if (line.charAt(i) == '>') {
				endIndex = i;
			}
		}
		return endIndex;
	}

	public static String getClassSimpleName(String fullClassName) {
		String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf("/") + 1,
				fullClassName.length());
		int startGenericIndex = simpleClassName.indexOf("<");
		if (startGenericIndex != -1) {
			simpleClassName = simpleClassName.substring(0, startGenericIndex);
		}
		return simpleClassName;
	}

	public static boolean isInstantRunEnabled(String projectPath) {
		File unknownDir = new File(projectPath, "unknown");
		if (unknownDir.exists() && unknownDir.isDirectory()) {
			for (File file : unknownDir.listFiles()) {
				if (file.getName().equals("instant-run.zip")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isSmaliFile(File file) {
		return file.isFile() && file.getName().endsWith(".smali");
	}
}
