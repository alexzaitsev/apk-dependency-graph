package com.alex_zaitsev.adg;

import com.alex_zaitsev.adg.FilterProvider;
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
        // parse arguments
        Arguments arguments = new ArgumentReader(args).read();
        if (arguments == null) {
            System.err.println("Arguments cannot be null!");
            return;
        }
        if (arguments.getFiltersPath() == null) {
            System.err.println("Please specify path to filter.json file.");
            return;
        }
        // parse filters
        Filters filters = new FiltersReader(arguments.getFiltersPath()).read();
        if (filters == null) {
            return;
        }

        // Delete the output directory for a better decoding result.
        if (FileUtils.deleteDir(arguments.getProjectPath())) {
            System.out.println("The output directory was deleted!");
        }

        // Decode the APK file for smali code in the output directory.
        ApkSmaliDecoderController.decode(
            arguments.getApkFilePath(), arguments.getProjectPath());

        // Analyze the decoded files and create the result file.
        FilterProvider filterProvider = new FilterProvider(filters);
        Filter<String> pathFilter = filterProvider.makePathFilter();
        Filter<String> classFilter = filterProvider.makeClassFilter();
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments, filters, 
                                                   pathFilter, classFilter);

        if (analyzer.run()) {
            File resultFile = new File(arguments.getResultPath());
            new Writer(resultFile).write(analyzer.getDependencies());
            System.out.println("Success! Now open gui/index.html in your browser.");
        }
    }
}