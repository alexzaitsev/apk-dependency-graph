package code.filter;

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
}