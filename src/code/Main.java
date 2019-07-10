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

import java.io.File;

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
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments, pathFilter, classFilter);
        if (analyzer.run()) {
            File resultFile = new File(arguments.getResultPath());
            new Writer(resultFile).write(analyzer.getDependencies());
            System.out.println("Success! Now open index.html in your browser.");
        }
    }

    private static Filter<String> getPathFilter(Filters inputFilters) {
        RegexFilter pathFilter = new RegexFilter("." + inputFilters.getPackageName() + ".");
        return pathFilter;
    }

    private static Filter<String> getClassFilter(Filters inputFilters) {
        RegexFilter classFilter = new RegexFilter(inputFilters.getIgnoredClasses());
        return classFilter;
    }
}