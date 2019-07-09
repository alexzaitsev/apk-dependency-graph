package code.io;

public class Filters {

    public static final boolean DEFAULT_PROCESS_INNER = true;

    private String packageName = null;
    private boolean processInner = DEFAULT_PROCESS_INNER;
    private String ignoredClassesRegex = null;

    public Filters(String packageName, boolean processInner, 
                   String ignoredClassesRegex) {
        this.packageName = packageName;
        this.processInner = processInner;
        this.ignoredClassesRegex = ignoredClassesRegex;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean getProcessInner() {
        return processInner;
    }

    public String getIgnoredClassesRegex() {
        return ignoredClassesRegex;
    }
}