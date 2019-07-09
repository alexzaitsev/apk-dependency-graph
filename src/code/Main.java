package code;

import code.decode.ApkSmaliDecoderController;
import code.io.ArgumentReader;
import code.io.Arguments;
import code.io.FiltersReader;
import code.io.Filters;
import code.io.Writer;
import code.util.FileUtils;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = new ArgumentReader(args).read();
        if (arguments == null) {
            System.err.println("Arguments cannot be null!");
            return;
        }
        Filters filters = null;
        if (arguments.getFiltersPath() != null) {
            filters = new FiltersReader(arguments.getFiltersPath()).read();
        }

        // Delete the output directory for a better decoding result.
        if (FileUtils.deleteDir(arguments.getProjectPath())) {
            System.out.println("The output directory was deleted!");
        }

        // Decode the APK file for smali code in the output directory.
        ApkSmaliDecoderController.decode(
            arguments.getApkFilePath(), arguments.getProjectPath());

        File resultFile = new File(arguments.getResultPath());
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments);
        if (analyzer.run()) {
            new Writer(resultFile).write(analyzer.getDependencies());
            System.out.println("Success! Now open index.html in your browser.");
        }
    }
}