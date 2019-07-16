package com.alex_zaitsev.adg;

import com.alex_zaitsev.adg.io.Filters;
import com.alex_zaitsev.adg.filter.Filter;
import com.alex_zaitsev.adg.filter.RegexFilter;
import com.alex_zaitsev.adg.filter.InverseRegexFilter;
import com.alex_zaitsev.adg.filter.AndFilter;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterProvider {

    private Filters inputFilters;

    public FilterProvider(Filters inputFilters) {
        this.inputFilters = inputFilters;
    }

    public Filter<String> makePathFilter() {
        if (inputFilters.getPackageName() == null || inputFilters.getPackageName().isEmpty()) {
            return null;
        }

        String replacement = Matcher.quoteReplacement(File.separator);
		String searchString = Pattern.quote(".");
        String packageNameAsPath = inputFilters.getPackageName().replaceAll(searchString, replacement);
        String packageNameRegex = ".*" + packageNameAsPath + ".*";
        RegexFilter filter = new RegexFilter(packageNameRegex);
        
        return filter;
    }

    public Filter<String> makeClassFilter() {
        String[] ignoredClasses = inputFilters.getIgnoredClasses();
        InverseRegexFilter ignoredClassesFilter = new InverseRegexFilter(ignoredClasses);

        return ignoredClassesFilter;
    }
}