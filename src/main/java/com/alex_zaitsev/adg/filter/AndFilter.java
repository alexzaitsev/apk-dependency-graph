package com.alex_zaitsev.adg.filter;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that holds filters chain
 */
public class AndFilter<T> extends Filter<T> {

    private List<Filter<T>> filters;

    public AndFilter(Filter<T>... filters) {
        this.filters = new ArrayList<>(Arrays.asList(filters));
    }

    public void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    /**
     * Filters the given object
     * 
     * @return true if ALL filters are satisfied, false otherwise
     */
    public boolean filter(T obj) {
        for (Filter<T> filter: filters) {
            if (!filter.filter(obj)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        for (int i = 0; i < filters.size(); i++) {
            builder.append(filters.get(i).toString());
            if (i != filters.size() - 1) builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }
}