package com.alex_zaitsev.adg.io;

import java.io.File;

public class ArgumentReader {

	private static final String ARG_PROJ = "-i";
	private static final String ARG_RES_JS = "-o";
	private static final String ARG_APK = "-a";
	private static final String ARG_FILTER = "-f";

	private static final String USAGE_STRING = "Usage:\n" +
			ARG_PROJ + " path : path to the decompiled project\n" +
			ARG_RES_JS + " path : path to the result js file\n" +
			ARG_APK + " path : path to the apk file\n" +
			ARG_FILTER + " filters : path to the your_filterset.json file";
	
	private String[] args;
	
	public ArgumentReader(String[] args) {
		this.args = args;
	}

	public Arguments read() {
		String projectPath = null, resultPath = null, apkPath = null, filtersPath = null;

		for (int i = 0; i < args.length; i++) {
			if (i < args.length - 1) {
				if (args[i].equals(ARG_PROJ)) {
					projectPath = args[i + 1];
				} else if (args[i].equals(ARG_RES_JS)) {
					resultPath = args[i + 1];
				} else if (args[i].equals(ARG_APK)) {
                    apkPath = args[i + 1];
                } else if (args[i].equals(ARG_FILTER)) {
					filtersPath = args[i + 1];
				}
			}
		}
		if (projectPath == null || resultPath == null || apkPath == null) {
			System.err.println(ARG_PROJ + ", " + ARG_RES_JS + " and " + ARG_APK + " must be provided!");
			System.err.println(USAGE_STRING);
			return null;
		}

		if (!checkFiles(new String[] {apkPath})) {
			return null;
		}
		if (filtersPath != null && !checkFiles(new String[] {filtersPath})) {
			return null;
		}

		return new Arguments(apkPath, projectPath, resultPath, filtersPath);
	}

	private boolean checkFiles(String[] files) {
		for (String fileName: files) {
			File file = new File(fileName);
			if (!file.exists()) {
				System.err.println(file + " is not found!");
				return false;
			}
		}
		return true;
	}
}
