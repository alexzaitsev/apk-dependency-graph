package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import code.io.Arguments;
import code.io.Filters;
import code.filter.Filter;

import static code.util.CodeUtils.isClassR;
import static code.util.CodeUtils.isClassGenerated;
import static code.util.CodeUtils.isClassInner;
import static code.util.CodeUtils.getOuterClass;
import static code.util.CodeUtils.isClassAnonymous;
import static code.util.CodeUtils.getAnonymousNearestOuter;
import static code.util.CodeUtils.getEndGenericIndex;
import static code.util.CodeUtils.getClassSimpleName;

public class SmaliAnalyzer {

	private Arguments arguments;
	private Filters filters;
	private Filter<String> pathFilter;
	private Filter<String> classFilter;

	public SmaliAnalyzer(Arguments arguments, 
						 Filters filters,
						 Filter<String> pathFilter,
						 Filter<String> classFilter) {
		this.arguments = arguments;
		this.filters = filters;
		this.pathFilter = pathFilter;
		this.classFilter = classFilter;
	}

	private Map<String, Set<String>> dependencies = new HashMap<>();

	public Map<String, Set<String>> getDependencies() {
		if (filters == null || filters.isProcessingInner()) {
			return dependencies;
		}
		return getFilteredDependencies();
	}

	public boolean run() {
		System.out.println("Analyzing dependencies...");
		
		File projectFolder = getProjectFolder();
		if (projectFolder.exists()) {
			traverseSmaliCode(projectFolder);
			return true;
		} else if (isInstantRunEnabled()){
			System.err.println("Enabled Instant Run feature detected. We cannot decompile it. Please, disable Instant Run and rebuild your app.");
		} else {
			System.err.println(projectFolder + " cannot be absent!");
		}
		return false;
	}

	private File getProjectFolder() {
		return new File(arguments.getProjectPath());
	}

	private boolean isInstantRunEnabled() {
		File unknownFolder = new File(arguments.getProjectPath() + File.separator + "unknown");
		if (unknownFolder.exists()) {
			for (File file : unknownFolder.listFiles()) {
				if (file.getName().equals("instant-run.zip")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void traverseSmaliCode(File folder) {
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File currentFile = listOfFiles[i];
			if (currentFile.isFile() && currentFile.getName().endsWith(".smali")) {
				if (pathFilter == null || pathFilter.filter(currentFile.getAbsolutePath())) {
					processSmaliFile(currentFile);
				}
			} else if (currentFile.isDirectory()) {
				traverseSmaliCode(currentFile);
			}
		}
	}

	private void processSmaliFile(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
			
			if (isClassR(fileName)) {
				return;
			}
			
			if (isClassAnonymous(fileName)) {
				fileName = getAnonymousNearestOuter(fileName);
			}

			Set<String> classNames = new HashSet<>();
			Set<String> dependencyNames = new HashSet<>();

			for (String line; (line = br.readLine()) != null;) {
				try {
					classNames.clear();
	
					parseAndAddClassNames(classNames, line);
	
					// filtering
					for (String fullClassName : classNames) {
						if (fullClassName != null && classFilter.filter(fullClassName)) {

							String simpleClassName = getClassSimpleName(fullClassName);
							if (isClassOk(simpleClassName, fileName)) {
								dependencyNames.add(simpleClassName);
							}
						}
					}
				} catch (Exception e) {
				}
			}

			// inner/nested class always depends on the outer class
			if (isClassInner(fileName)) {
				dependencyNames.add(getOuterClass(fileName));
			}

			if (!dependencyNames.isEmpty()) {
				addDependencies(fileName, dependencyNames);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cannot found " + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Cannot read " + file.getAbsolutePath());
		}
	}
	
	/**
	 * The last filter. Do not show anonymous classes (their dependencies belongs to outer class), 
	 * generated classes, avoid circular dependencies, do not show generated R class
	 * @param simpleClassName class name to inspect
	 * @param fileName full class name
	 * @return true if class is good with these conditions
	 */
	private boolean isClassOk(String simpleClassName, String fileName) {
		return !isClassAnonymous(simpleClassName) && !isClassGenerated(simpleClassName)
				&& !fileName.equals(simpleClassName) && !isClassR(simpleClassName);
	}
	
	private void parseAndAddClassNames(Set<String> classNames, String line) {
		int index = line.indexOf("L");
		while (index != -1) {
			int colonIndex = line.indexOf(";", index);
			if (colonIndex == -1) {
				break;
			}

			String className = line.substring(index + 1, colonIndex);
			if (className.matches("[\\w\\d/$<>]*")) {
				int startGenericIndex = className.indexOf("<");
				if (startGenericIndex != -1 && className.charAt(startGenericIndex + 1) == 'L') {
					// generic
					int startGenericInLineIndex = index + startGenericIndex + 1; // index of "<" in the original string
					int endGenericInLineIndex = getEndGenericIndex(line, startGenericInLineIndex);
					String generic = line.substring(startGenericInLineIndex + 1, endGenericInLineIndex);
					parseAndAddClassNames(classNames, generic);
					index = line.indexOf("L", endGenericInLineIndex);
					className = className.substring(0, startGenericIndex);
				} else {
					index = line.indexOf("L", colonIndex);
				}
			} else {
				index = line.indexOf("L", index+1);
				continue;
			}

			classNames.add(className);
		}
	}	

	private void addDependencies(String className, Set<String> dependenciesList) {
		Set<String> depList = dependencies.get(className);
		if (depList == null) {
			// add this class and its dependencies
			dependencies.put(className, dependenciesList);
		} else {
			// if this class is already added - update its dependencies
			depList.addAll(dependenciesList);
		}
	}

	private Map<String,Set<String>> getFilteredDependencies() {
		Map<String, Set<String>> filteredDependencies = new HashMap<>();
		for (String key : dependencies.keySet()) {
			if (!key.contains("$")) {
				Set<String> dependencySet = new HashSet<>();
				for (String dependency : dependencies.get(key)) {
					if (!dependency.contains("$")) {
						dependencySet.add(dependency);
					}
				}
				if (dependencySet.size() > 0) {
					filteredDependencies.put(key, dependencySet);
				}
			}
		}
		return filteredDependencies;
	}
}
