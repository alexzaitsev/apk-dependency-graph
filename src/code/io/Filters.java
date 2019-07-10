package code.io;

public class Filters {

    public static final boolean DEFAULT_PROCESS_INNER = false;

    private String packageName = null;
    private boolean processingInner = DEFAULT_PROCESS_INNER;
    private String[] ignoredClasses = null;

    public Filters(String packageName, boolean processingInner, 
                   String[] ignoredClasses) {
        this.packageName = packageName;
        this.processingInner = processingInner;
        this.ignoredClasses = ignoredClasses;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isProcessingInner() {
        return processingInner;
    }

    public String[] getIgnoredClasses() {
        return ignoredClasses;
    }
}