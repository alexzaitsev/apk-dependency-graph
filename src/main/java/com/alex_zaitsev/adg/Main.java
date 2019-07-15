package com.alex_zaitsev.adg;

import static com.alex_zaitsev.adg.FilterProvider.makePathFilter;
import static com.alex_zaitsev.adg.FilterProvider.makeClassFilter;

import com.alex_zaitsev.adg.decode.ApkSmaliDecoderController;
import com.alex_zaitsev.adg.io.ArgumentReader;
import com.alex_zaitsev.adg.io.Arguments;
import com.alex_zaitsev.adg.io.FiltersReader;
import com.alex_zaitsev.adg.io.Filters;
import com.alex_zaitsev.adg.io.Writer;
import com.alex_zaitsev.adg.util.FileUtils;
import com.alex_zaitsev.adg.filter.Filter;

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
        Filter<String> pathFilter = filters == null ? null : makePathFilter(filters);
        Filter<String> classFilter = filters == null ? null : makeClassFilter(filters);
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments, filters, 
                                                   pathFilter, classFilter);

        if (analyzer.run()) {
            File resultFile = new File(arguments.getResultPath());
            new Writer(resultFile).write(analyzer.getDependencies());
            System.out.println("Success! Now open index.html in your browser.");
        }
    }
}