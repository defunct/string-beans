package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

public interface MetaObject {
    public Class<?> getObjectClass();
    public Object newInstance();
    public boolean isScalar();

    /**
     * Returns a iterator over the readable properties in the object. For beans,
     * those properties that have setters but no getters are excluded.
     * 
     * @param object
     *            The object with property to iterate.
     * @return An iterator over the readable properties in the given object.
     */
    public Iterable<ObjectBucket> buckets(Object object);
    public void set(Object object, String name, Object value);
    
    // FIXME To support a set of types, this will have to take a name and a
    // class. Only if the set is a singleton set, can the class be null.
    public Type getPropertyType(String name);
}
