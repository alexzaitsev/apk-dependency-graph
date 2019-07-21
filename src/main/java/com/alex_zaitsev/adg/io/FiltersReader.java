package com.alex_zaitsev.adg.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FiltersReader {

    private static final String FILTER_PACKAGE_NAME = "package-name";
	private static final String FILTER_SHOW_INNER = "show-inner-classes";
    private static final String FILTER_IGNORED_CLASSES = "ignored-classes";
    
    private String filtersFilePath;

    public FiltersReader(String filtersFilePath) {
        this.filtersFilePath = filtersFilePath;
    }

    /**
     * Parses the your_filterset.json file and produces Filters object
     */
    public Filters read() {
        String packageName = null;
        boolean showInnerClasses = Filters.DEFAULT_PROCESS_INNER;
        String[] ignoredClassesArr = null;

		try {
			String content = new String(Files.readAllBytes(Paths.get(filtersFilePath)));
			String mainObject = content.replace("{", "").replace("}", "").trim();
			String[] rawParams = mainObject.split(",");
            
			for (String rawParam: rawParams) {
				if (rawParam.contains(FILTER_PACKAGE_NAME)) {
                    packageName = rawParam.trim().split(":")[1].trim().replace("\"", "");
                }
                if (rawParam.contains(FILTER_SHOW_INNER)) {
                    showInnerClasses = Boolean.valueOf(rawParam.trim().split(":")[1].trim().replace("\"", ""));
                }
            }
            if (mainObject.contains(FILTER_IGNORED_CLASSES)) {
                String ignoredClasses = mainObject.substring(mainObject.indexOf('[') + 1, mainObject.lastIndexOf(']'));
                ignoredClassesArr = ignoredClasses.split(",");
                for (int i = 0; i < ignoredClassesArr.length; i++) {
                    ignoredClassesArr[i] = ignoredClassesArr[i].replace("\"", "").trim();
                }
            }
		} catch (Exception e) {
			System.err.println("An error happened during " + filtersFilePath + " processing!");
			e.printStackTrace();
			return null;
        }

        if (packageName == null || packageName.isEmpty()) {
            System.err.println("'package-name' option cannot be empty. Check " + filtersFilePath);
            return null;
        }
        if (showInnerClasses) {
            System.out.println("Warning! Processing including inner classes.");
        }
        if (ignoredClassesArr == null) {
            System.out.println("Warning! Processing without class filtering.");
        }

        return new Filters(packageName, showInnerClasses, ignoredClassesArr);
	}
}