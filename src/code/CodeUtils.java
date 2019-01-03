package code;

import code.util.StringUtils;

public class CodeUtils {

	public static boolean isClassR(String className) {
		return className != null && className.equals("R") || className.startsWith("R$");
	}

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
}
