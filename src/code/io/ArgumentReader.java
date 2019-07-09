package code.io;

import java.io.File;

public class ArgumentReader {

	private static final ARG_PROJ = "-i";
	private static final ARG_RES_JS = "-o";
	private static final ARG_PACK_F = "-pf";
	private static final ARG_FILTER = "-f";
	private static final ARG_INNER = "-d";
	private static final ARG_APK = "-a";

	private static final String USAGE_STRING = "Usage:\n" +
			ARG_PROJ + " path : path to the decompiled project\n" +
			ARG_RES_JS + " path : path to the result js file\n" +
			ARG_PACK_F + " package-filter : java package to filter by (to show all dependencies pass '-f nofilter')\n" +
			ARG_FILTER + " filters : json filterset file(s) (can be comma-separated)\n"
			ARG_INNER + " boolean : if true it will contain inner class processing (the ones creating ClassName$InnerClass files)\n" +
			ARG_APK + " path : path to the apk file";
	
	private String[] args;
	
	public ArgumentReader(String[] args) {
		this.args = args;
	}

	public Arguments read() {
		String projectPath = null, resultPath = null, packageFilter = null;
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
                apkPath, projectPath, resultPath, packageFilter, withInnerClasses);
	}
}
