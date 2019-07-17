package com.alex_zaitsev.adg.filter;

public class InverseRegexFilter extends RegexFilter {

    public InverseRegexFilter(String regex) {
        super(regex);
    }

    public InverseRegexFilter(String[] regex) {
        super(regex);
    }

    /**
     * @return true if String doesn't match the given regex, false otherwise
     */
    public boolean filter(String obj) {
        return !super.filter(obj);
    }
}