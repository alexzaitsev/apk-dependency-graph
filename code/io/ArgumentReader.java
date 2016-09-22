package code.io;

import java.io.File;

public class ArgumentReader {

	private static final String USAGE_STRING = "Usage:\n-i path : path to the decompiled project\n-o path : path to the result js file\n-f filter : java package to filter by (to show all dependencies pass '-f nofilter')";
	
	private String[] args;
	
	public ArgumentReader(String[] args) {
		this.args = args;
	}

	public Arguments read() {
		if (args.length != 4 && args.length != 6) {
			System.err.println(USAGE_STRING);
			return null;
		}
		String projectPath = null, resultPath = null, filter = null;
		for (int i = 0; i < args.length; i++) {
			if (i < args.length - 1) {
				if (args[i].equals("-i")) {
					projectPath = args[i + 1];
				}
				if (args[i].equals("-o")) {
					resultPath = args[i + 1];
				}
				if (args[i].equals("-f")) {
					filter = args[i + 1];
				}
			}
		}
		if (projectPath == null || resultPath == null || filter == null) {
			System.err.println("Arguments are incorrect!");
			System.err.println(USAGE_STRING);
			return null;
		}
		if (filter.equals("nofilter")) {
			filter = null;
			System.out.println("Warning! Processing without filter.");
		}
		File projectFile = new File(projectPath);
		if (!projectFile.exists()) {
			System.err.println(projectPath + " doesn't exist!");
			return null;
		}
		if (!projectFile.isDirectory()) {
			System.err.println(projectPath + " must be a directory!");
			return null;
		}
		return new Arguments(projectPath, resultPath, filter);
	}
}
