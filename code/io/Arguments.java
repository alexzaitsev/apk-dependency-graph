package code.io;

public class Arguments {

	private String projectPath;
	private String resultPath;
	private String filter;
	
	public Arguments(String projectPath, String resultPath, String filter) {
		super();
		this.projectPath = projectPath;
		this.resultPath = resultPath;
		this.filter = filter;
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
}
