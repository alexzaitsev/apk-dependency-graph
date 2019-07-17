package com.alex_zaitsev.adg.decode;

import com.alex_zaitsev.adg.util.ZipFileUtils;

import org.jf.baksmali.Baksmali;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.analysis.InlineMethodResolver;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;

import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.util.List;

public final class ApkSmaliDecoder {
    private static final int MAXIMUM_NUMBER_OF_PROCESSORS = 6;

    private static final String DEX_FILE_EXTENSION = ".dex";

    private static final String WARNING_DISASSEMBLING_ODEX_FILE =
        "Warning: You are disassembling an odex file without deodexing it.";
    private static final String WARNING_FILE_IS_NOT_FOUND =
        "Apk file is not found!";

    private final String apkFilePath;
    private final String outDirPath;
    private final int apiVersion;

    ApkSmaliDecoder(String apkFilePath, String outDirPath, int api) {
        this.apkFilePath = apkFilePath;
        this.outDirPath  = outDirPath;
        this.apiVersion  = api;
    }

    void decode() throws IOException {
        File apkFile = new File(this.apkFilePath);
        if (!apkFile.exists()) {
            throw new IOException(WARNING_FILE_IS_NOT_FOUND);
        }
        File outDir = new File(this.outDirPath);

        // Read all dex files in the APK file and so decode each one.
        for (String dexFileName : getDexFiles(this.apkFilePath)) {
            decodeDexFile(apkFile, dexFileName, this.apiVersion, outDir);
        }
    }

    private void decodeDexFile(
            File apkFile, String dexFileName, int apiVersion, File outDir)
            throws IOException {
        try {
            log("Baksmaling " + dexFileName + "...");
            DexBackedDexFile dexFile =
                loadDexFile(apkFile, dexFileName, apiVersion);

            Baksmali.disassembleDexFile(
                dexFile,
                outDir,
                getNumberOfAvailableProcessors(),
                getSmaliOptions(dexFile));
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private int getNumberOfAvailableProcessors() {
        int jobs = Runtime.getRuntime().availableProcessors();
        return Math.min(jobs, MAXIMUM_NUMBER_OF_PROCESSORS);
    }

    private BaksmaliOptions getSmaliOptions(final DexBackedDexFile dexFile) {
        final BaksmaliOptions options = new BaksmaliOptions();

        options.deodex = false;
        options.implicitReferences = false;
        options.parameterRegisters = true;
        options.localsDirective = true;
        options.sequentialLabels = true;
        options.debugInfo = false;
        options.codeOffsets = false;
        options.accessorComments = false;
        options.registerInfo = 0;

        if (dexFile instanceof DexBackedOdexFile) {
            options.inlineResolver =
                    InlineMethodResolver.createInlineMethodResolver(
                        ((DexBackedOdexFile)dexFile).getOdexVersion());
        } else {
            options.inlineResolver = null;
        }

        return options;
    }

    private DexBackedDexFile loadDexFile(
            File apkFile, String dexFilePath, int apiVersion)
            throws IOException {
        DexBackedDexFile dexFile = DexFileFactory.loadDexEntry(
            apkFile, dexFilePath, true, Opcodes.forApi(apiVersion));

        if (dexFile == null || dexFile.isOdexFile()) {
            throw new IOException(WARNING_DISASSEMBLING_ODEX_FILE);
        }

        return dexFile;
    }

    private List<String> getDexFiles(String apkFilePath) throws IOException {
        return ZipFileUtils.filterByExtension(apkFilePath, DEX_FILE_EXTENSION);
    }

    private void log(final String text) {
        System.out.println(text);
    }
}