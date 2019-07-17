package com.alex_zaitsev.adg.io;

public class Arguments {
    
    private String apkFilePath;
    private String projectPath;
    private String resultPath;
    private String filtersPath;

    public Arguments(String apkPath, String projectPath, String resultPath,
            String filtersPath) {
        this.apkFilePath = apkPath;
        this.projectPath = projectPath;
        this.resultPath = resultPath;
        this.filtersPath = filtersPath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public String getApkFilePath() {
        return this.apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public String getFiltersPath() {
        return filtersPath;
    }

    public void setFiltersPath(String filtersPath) {
        this.filtersPath = filtersPath;
    }
}
