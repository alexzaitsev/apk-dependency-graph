package code.io;

import java.io.File;

public class ArgumentReader {

	private static final String USAGE_STRING = "Usage:\n" +
			"-i path : path to the decompiled project\n" +
			"-o path : path to the result js file\n" +
			"-f filter : java package to filter by (to show all dependencies pass '-f nofilter')\n" +
			"-d boolean : if true it will contain inner class processing (the ones creating ClassName$InnerClass files)\n" +
            "-a path : path to the apk file";
	
	private String[] args;
	
	public ArgumentReader(String[] args) {
		this.args = args;
	}

	public Arguments read() {
		String projectPath = null, resultPath = null, filter = null;
        String apkPath = null;
		boolean withInnerClasses = false;
		for (int i = 0; i < args.length; i++) {
			if (i < args.length - 1) {
				if (args[i].equals("-i")) {
					projectPath = args[i + 1];
				} else if (args[i].equals("-o")) {
					resultPath = args[i + 1];
				} else if (args[i].equals("-f")) {
					filter = args[i + 1];
				} else if (args[i].equals("-d")) {
					withInnerClasses = Boolean.valueOf(args[i + 1]);
				} else if (args[i].equals("-a")) {
                    apkPath = args[i + 1];
                }
			}
		}
		if (projectPath == null || resultPath == null || filter == null ||
                apkPath == null) {
			System.err.println("Arguments are incorrect!");
			System.err.println(USAGE_STRING);
			return null;
		}
		if (filter.equals("nofilter")) {
			filter = null;
			System.out.println("Warning! Processing without filter.");
		}
		if (!withInnerClasses) {
			System.out.println("Warning! Processing without inner classes.");
		}

        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            System.out.println(apkFile + " is not found!");
            return null;
        }

        return new Arguments(
                apkPath, projectPath, resultPath, filter, withInnerClasses);
	}
}
