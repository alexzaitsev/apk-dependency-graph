package code.filter;

public class RegexFilter extends Filter<String> {

    private String regex;

    public RegexFilter(String regex) {
        this.regex = regex;
    }

    public RegexFilter(String[] regex) {
        for (int i = 0; i < regex.length; i++) {
            regex[i] = "(" + regex[i] + ")";
        }
        this.regex = String.join("|", regex);
    }

    /**
     * @return true if String matches the given regex, false otherwise
     */
    public boolean filter(String obj) {
        return obj.matches(regex);
    }
}