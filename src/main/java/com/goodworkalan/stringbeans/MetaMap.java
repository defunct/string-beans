package com.goodworkalan.stringbeans;

import static com.goodworkalan.stringbeans.StringBeanException.$;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Performs the basic operations of serialization on a map type.
 * 
 * @author Alan Gutierrez
 */
public class MetaMap implements MetaObject {
    /** The actualized parameterized map type. */
    private final ParameterizedType pt;

    /**
     * Create an instance of map meta information.
     * 
     * @param pt
     *            The actualized parameterized map type.
     */
    public MetaMap(ParameterizedType pt) {
        this.pt = pt;
    }

    /**
     * Get the map type for this meta map.
     * 
     * @return The map type.
     */
    public Class<?> getObjectClass() {
        return (Class<?>) pt.getRawType();
    }

    /**
     * Get the value type for this meta map.
     * 
     * @param name
     *            The property name.
     * @return The value type.
     */
    public Type getPropertyType(String name) {
        return pt.getActualTypeArguments()[1];
    }

    /**
     * Return false indicating that maps are not scalars.
     * 
     * @return False indicating that maps are not scalars.
     */
    public boolean isScalar() {
        return false;
    }

    /**
     * Create a new instance of a map to place on the object stack for
     * population during parsing. If the map type is class, a new instance of
     * the class is created using the default constructor of the class. If the
     * type is <code>SortedMap</code> then a <code>TreeMap</code> is created. If
     * the type is <code>Map</code> then a <code>LinkedHashMap</code> is
     * created.
     * 
     * @return A new map instance.
     */
    public Object newStackInstance() {
        Class<?> mapClass = (Class<?>) pt.getRawType();
        if (!mapClass.isInterface()) {
            try {
                return mapClass.newInstance();
            } catch (Throwable e) {
                throw new StringBeanException($(e), MetaMap.class, "newInstance");
            }
        } else if (SortedMap.class.isAssignableFrom(mapClass)) {
            return new TreeMap<Object, Object>();
        }
        return new LinkedHashMap<Object, Object>();
    }

    /**
     * Returns the given <code>object</code> since we returned a map object of
     * the appropriate type from {@link #newStackInstance() newStackInstance}.
     * 
     * @param participants
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param object
     *            The object taken from the stack.
     * @return The given object.
     */
    public Object newInstance(Map<Object, Object> participants, Object object) {
        return object;
    }

    /**
     * Put the given name and value into the given map object.
     * 
     * @param object
     *            The map.
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Map) object).put(name, value);
    }
}
