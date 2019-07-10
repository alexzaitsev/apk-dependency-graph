package code.filter;

public class RegexFilter extends Filter<String> {

    protected String regex;

    public RegexFilter(String regex) {
        this.regex = regex;
    }

    public RegexFilter(String[] regex) {
        this.regex = String.join("|", regex);
    }

    /**
     * @return true if String matches the given regex, false otherwise
     */
    public boolean filter(String obj) {
        return obj.matches(regex);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{").append(regex).append("}");
        return builder.toString();
    }
}