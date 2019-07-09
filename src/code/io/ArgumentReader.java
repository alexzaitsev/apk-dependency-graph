package code.io;

import java.io.File;

public class ArgumentReader {

	private static final String ARG_PROJ = "-i";
	private static final String ARG_RES_JS = "-o";
	private static final String ARG_PACK_F = "-pf";
	private static final String ARG_FILTER = "-f";
	private static final String ARG_INNER = "-d";
	private static final String ARG_APK = "-a";

	private static final String USAGE_STRING = "Usage:\n" +
			ARG_PROJ + " path : path to the decompiled project\n" +
			ARG_RES_JS + " path : path to the result js file\n" +
			ARG_PACK_F + " package-filter : java package to filter by (to show all dependencies pass '" + ARG_PACK_F + " nofilter')\n" +
			ARG_FILTER + " filters : path to the filters.json file\n" +
			ARG_INNER + " boolean : if true it will contain inner class processing (the ones creating ClassName$InnerClass files)\n" +
			ARG_APK + " path : path to the apk file";
	
	private String[] args;
	
	public ArgumentReader(String[] args) {
		this.args = args;
	}

	public Arguments read() {
		String projectPath = null, resultPath = null, packageFilter = null, filtersPath = null;
        String apkPath = null;
		boolean withInnerClasses = false;

		for (int i = 0; i < args.length; i++) {
			if (i < args.length - 1) {
				if (args[i].equals(ARG_PROJ)) {
					projectPath = args[i + 1];
				} else if (args[i].equals(ARG_RES_JS)) {
					resultPath = args[i + 1];
				} else if (args[i].equals(ARG_PACK_F)) {
					packageFilter = args[i + 1];
				} else if (args[i].equals(ARG_INNER)) {
					withInnerClasses = Boolean.valueOf(args[i + 1]);
				} else if (args[i].equals(ARG_APK)) {
                    apkPath = args[i + 1];
                } else if (args[i].equals(ARG_FILTER)) {
					filtersPath = args[i + 1];
				}
			}
		}
		if (projectPath == null || resultPath == null || packageFilter == null ||
                apkPath == null) {
			System.err.println("Arguments are incorrect!");
			System.err.println(USAGE_STRING);
			return null;
		}
		if (packageFilter.equals("nofilter")) {
			packageFilter = null;
			System.out.println("Warning! Processing without package filter.");
		}
		if (!withInnerClasses) {
			System.out.println("Warning! Processing without inner classes.");
		}

		String[] filesToCheck = new String[] {projectPath, apkPath, filtersPath};
		if (!checkFiles(filesToCheck)) {
			return null;
		}

		String filterRegex = parseFilters(filtersPath);

		return new Arguments(apkPath, projectPath, resultPath, packageFilter, 
			withInnerClasses, filterRegex);
	}

	private boolean checkFiles(String[] files) {
		for (String fileName: files) {
			File file = new File(fileName);
			if (!file.exists()) {
				System.out.println(file + " is not found!");
				return false;
			}
		}
		return true;
	}

	private String parseFilters(String filterFile) {
		

		return "(^R$)|(^R\\$)|(.*\\$_ViewBinding$)|(.*\\$\\$ViewInjector$)"; // TODO
	}
}
