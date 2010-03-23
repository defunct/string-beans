package com.goodworkalan.stringbeans;

import java.util.Set;

/**
 * String beans emitter and parser configuration.
 *
 * @author Alan Gutierrez
 */
public class Stringer {
    /** The set of beans that can be read or written. */
    private final Set<Class<?>> beans;
    
    /** The set of converters (need a default set). */
    private final Converter converter;

    /**
     * Create string beans configuration that will treat the given set of
     * classes as beans to serialize and use the given converter to convert all
     * other classes to strings.
     * 
     * @param beans
     *            The set of classes that are beans.
     * @param converter
     *            The object to string converter.
     */
    public Stringer(Set<Class<?>> beans, Converter converter) {
        this.beans = beans;
        this.converter = converter;
    }

    /**
     * Return true of the given class is considered a bean by this string bean
     * configuration. A bean will be inspected using reflection and all all
     * properties and fields will be serialized as name value pairs.
     * 
     * @param type
     *            The class type.
     * @return True if the given class is considered a bean by this string bean
     *         configuration.
     */
    public boolean isBean(Class<?> type) {
        return beans.contains(type);
    }

    /**
     * Get the converter used to convert objects into strings.
     * 
     * @return The converter.
     */
    public Converter getConverter() {
        return converter;
    }
}
