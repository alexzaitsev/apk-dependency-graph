package code.io;

public class Arguments {
    private String apkFilePath;
    private String projectPath;
    private String resultPath;
    private String filter;
    private boolean withInnerClasses;

    public Arguments(String apkPath, String projectPath, String resultPath,
            String filter, boolean withInnerClasses) {
        super();
        this.apkFilePath = apkPath;
        this.projectPath = projectPath;
        this.resultPath = resultPath;
        this.filter = filter;
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
    public String getFilter() {
        return filter;
    }
    public void setFilter(String filter) {
        this.filter = filter;
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
