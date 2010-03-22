package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

public class MetaScalar implements MetaObject {
    private final Class<?> objectClass;

    public MetaScalar(Class<?> objectClass) {
        this.objectClass = Converter.box(objectClass);
    }

    public Iterable<ObjectBucket> buckets(Object object) {
        throw new UnsupportedOperationException();
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public boolean isScalar() {
        return true;
    }

    public Type getPropertyType(String name) {
        throw new UnsupportedOperationException();
    }

    public Object newInstance() {
        throw new UnsupportedOperationException();
    }

    public void set(Object object, String name, Object value) {
        throw new UnsupportedOperationException();
    }
}
