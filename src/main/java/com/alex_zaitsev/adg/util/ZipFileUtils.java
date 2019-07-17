package com.alex_zaitsev.adg.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtils {
    public static List<String> filterByExtension(
            final String zipFilePath, final String extension)
            throws IOException {
        List<String> files = new ArrayList<>();
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
            if (!zipEntry.isDirectory()) {
                String fileName = zipEntry.getName();
                if (checkExtension(fileName, extension)) {
                    files.add(fileName);
                }
            }
        }
        return files;
    }

    private static boolean checkExtension(
            final String filePath, final String extension) {
        return filePath.endsWith(extension);
    }
}