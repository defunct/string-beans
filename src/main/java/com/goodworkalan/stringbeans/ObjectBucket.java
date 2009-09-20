package com.goodworkalan.stringbeans;

public class ObjectBucket {
    private final Class<?> propertyClass;

    private final String name;

    private final Object value;

    public ObjectBucket(Class<?> propertyClass, String name, Object value) {
        this.propertyClass = propertyClass;
        this.name = name;
        this.value = value;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
