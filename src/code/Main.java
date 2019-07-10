package code;

import code.decode.ApkSmaliDecoderController;
import code.io.ArgumentReader;
import code.io.Arguments;
import code.io.FiltersReader;
import code.io.Filters;
import code.io.Writer;
import code.util.FileUtils;
import code.filter.Filter;
import code.filter.RegexFilter;
import code.filter.InverseRegexFilter;
import code.filter.AndFilter;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = new ArgumentReader(args).read();
        if (arguments == null) {
            System.err.println("Arguments cannot be null!");
            return;
        }
        Filters filters = arguments.getFiltersPath() == null ? null :
            new FiltersReader(arguments.getFiltersPath()).read();

        // Delete the output directory for a better decoding result.
        if (FileUtils.deleteDir(arguments.getProjectPath())) {
            System.out.println("The output directory was deleted!");
        }

        // Decode the APK file for smali code in the output directory.
        ApkSmaliDecoderController.decode(
            arguments.getApkFilePath(), arguments.getProjectPath());

        // Analyze the decoded files and create the result file.
        Filter<String> pathFilter = filters == null ? null : getPathFilter(filters);
        Filter<String> classFilter = filters == null ? null : getClassFilter(filters);
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments, filters, 
                                                   pathFilter, classFilter);

        if (analyzer.run()) {
            File resultFile = new File(arguments.getResultPath());
            new Writer(resultFile).write(analyzer.getDependencies());
            System.out.println("Success! Now open index.html in your browser.");
        }
    }

    private static Filter<String> getPathFilter(Filters inputFilters) {
        if (inputFilters.getPackageName() == null || inputFilters.getPackageName().isEmpty()) {
            return null;
        }

        String replacement = Matcher.quoteReplacement(File.separator);
		String searchString = Pattern.quote(".");
        String packageNameAsPath = inputFilters.getPackageName().replaceAll(searchString, replacement);
        
        return new RegexFilter("." + packageNameAsPath + ".");
    }

    private static Filter<String> getClassFilter(Filters inputFilters) {
        String[] ignoredClasses = inputFilters.getIgnoredClasses();
        InverseRegexFilter ignoredClassesFilter = new InverseRegexFilter(ignoredClasses);

        AndFilter<String> andFilter = new AndFilter(ignoredClassesFilter);

        if (inputFilters.getPackageName() != null) {
            String packageNameRegex = "^" + inputFilters.getPackageName().replaceAll("\\.", "/");
            andFilter.addFilter(new RegexFilter(packageNameRegex));
        }

        return andFilter;
    }
}