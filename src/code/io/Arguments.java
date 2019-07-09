package code.io;

public class Arguments {
    private String apkFilePath;
    private String projectPath;
    private String resultPath;
    private String packageFilter;
    private boolean withInnerClasses;

    public Arguments(String apkPath, String projectPath, String resultPath,
            String packageFilter, boolean withInnerClasses) {
        super();
        this.apkFilePath = apkPath;
        this.projectPath = projectPath;
        this.resultPath = resultPath;
        this.packageFilter = packageFilter;
        this.withInnerClasses = withInnerClasses;
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
    public String getPackageFilter() {
        return packageFilter;
    }
    public void setPackageFilter(String packageFilter) {
        this.packageFilter = packageFilter;
    }
    public boolean withInnerClasses() {
        return withInnerClasses;
    }
    public void setWithInnerClasses(boolean withInnerClasses) {
        this.withInnerClasses = withInnerClasses;
    }
    public String getApkFilePath() {
        return this.apkFilePath;
    }
}
