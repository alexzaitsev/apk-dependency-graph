package com.alex_zaitsev.adg.util;

import java.io.File;

public class FileUtils {
    public static boolean deleteDir(final String dirPath) {
        File dirFile = new File(dirPath);
        return dirFile.exists() && deleteDirectory(dirFile);
    }

    private static boolean deleteDirectory(final File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return dir.delete();
    }
}
