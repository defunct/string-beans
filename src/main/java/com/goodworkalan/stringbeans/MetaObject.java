package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

public interface MetaObject {
    public Class<?> getObjectClass();
    public Object newInstance();
    public boolean isScalar();
    public Iterable<ObjectBucket> buckets(Object object);
    public void set(Object object, String name, Object value);
    public Type getPropertyType(String name);
}
