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

    public static Filter<String> makePathFilter(Filters inputFilters) {
        if (inputFilters.getPackageName() == null || inputFilters.getPackageName().isEmpty()) {
            return null;
        }

        String replacement = Matcher.quoteReplacement(File.separator);
		String searchString = Pattern.quote(".");
        String packageNameAsPath = inputFilters.getPackageName().replaceAll(searchString, replacement);
        
        RegexFilter filter = new RegexFilter(".*" + packageNameAsPath + ".*");
        
        return filter;
    }

    public static Filter<String> makeClassFilter(Filters inputFilters) {
        String[] ignoredClasses = inputFilters.getIgnoredClasses();
        InverseRegexFilter ignoredClassesFilter = new InverseRegexFilter(ignoredClasses);

        AndFilter<String> andFilter = new AndFilter(ignoredClassesFilter);

        if (inputFilters.getPackageName() != null) {
            String packageNameRegex = "^" + inputFilters.getPackageName().replaceAll("\\.", "/");
            andFilter.addFilter(new RegexFilter(packageNameRegex));
        }

        return andFilter;
    }
}