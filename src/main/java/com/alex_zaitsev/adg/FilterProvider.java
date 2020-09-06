package com.alex_zaitsev.adg;

import com.alex_zaitsev.adg.io.*;
import com.alex_zaitsev.adg.filter.*;

import org.apache.commons.lang3.SystemUtils;

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

	    // see https://github.com/alexzaitsev/apk-dependency-graph/issues/60
        String packageNameAsPath = inputFilters.getPackageName().replaceAll(SystemUtils.IS_OS_WINDOWS ? "/" : "\\.", replacement);
        String packageNameRegex = ".*" + packageNameAsPath + ".*";

        return new RegexFilter(packageNameRegex);
    }

    public Filter<String> makeClassFilter() {
        String[] ignoredClasses = inputFilters.getIgnoredClasses();

        return ignoredClasses == null ? null : new InverseRegexFilter(ignoredClasses);
    }
}