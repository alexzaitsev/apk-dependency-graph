package code.filter;

/**
 * Base filter class in the hierarchy
 */
public abstract class Filter<T> {

    /**
     * Filters the object
     * 
     * @return true if object satisfies conditions, false otherwise
     */
    public boolean filter(T obj);
}