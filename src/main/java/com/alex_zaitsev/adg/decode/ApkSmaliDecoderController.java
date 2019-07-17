package com.alex_zaitsev.adg.decode;

import java.io.IOException;

public class ApkSmaliDecoderController {
    // TODO: Change the default API version to the current version of
    // the APK, to be according to the APK version.
    private static final int DEFAULT_ANDROID_VERSION = 28;

    public static void decode(
            final String apkFilePath, final String outDirPath) {
        try {
            new ApkSmaliDecoder(
                apkFilePath, outDirPath, DEFAULT_ANDROID_VERSION).decode();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}