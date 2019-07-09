package code.io;

public class Arguments {
    
    private String apkFilePath;
    private String projectPath;
    private String resultPath;
    private String filtersPath;

    public Arguments(String apkPath, String projectPath, String resultPath,
            String filtersPath) {
        super();
        this.apkFilePath = apkPath;
        this.projectPath = projectPath;
        this.resultPath = resultPath;
        this.filtersPath = filtersPath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public String getApkFilePath() {
        return this.apkFilePath;
    }

    public String getFiltersPath() {
        return filtersPath;
    }
}
