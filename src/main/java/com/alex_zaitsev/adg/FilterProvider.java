package com.alex_zaitsev.adg;

import com.alex_zaitsev.adg.io.*;
import com.alex_zaitsev.adg.filter.*;

import java.io.File;
import java.util.regex.Matcher;

public class FilterProvider {

    private Filters inputFilters;

    public FilterProvider(Filters inputFilters) {
        this.inputFilters = inputFilters;
    }

    public Filter<String> makePathFilter() {
        String replacement = Matcher.quoteReplacement(File.separator);
	    replacement = Matcher.quoteReplacement(replacement);
        String packageNameAsPath = inputFilters.getPackageName().replaceAll("\\.", replacement);
        String packageNameRegex = ".*" + packageNameAsPath + ".*";
        RegexFilter filter = new RegexFilter(packageNameRegex);
        
        return filter;
    }

    public Filter<String> makeClassFilter() {
        String[] ignoredClasses = inputFilters.getIgnoredClasses();
        if (ignoredClasses == null) {
            return null;
        }

        InverseRegexFilter ignoredClassesFilter = new InverseRegexFilter(ignoredClasses);

        return ignoredClassesFilter;
    }
}