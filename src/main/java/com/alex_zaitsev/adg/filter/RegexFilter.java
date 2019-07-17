package com.alex_zaitsev.adg.filter;

import java.util.regex.Pattern;

public class RegexFilter extends Filter<String> {

    protected Pattern pattern;

    public RegexFilter(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public RegexFilter(String[] regex) {
        this(String.join("|", regex));
    }

    /**
     * @return true if String matches the given regex, false otherwise
     */
    public boolean filter(String obj) {
        return pattern.matcher(obj).matches();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{").append(pattern.toString()).append("}");
        return builder.toString();
    }
}