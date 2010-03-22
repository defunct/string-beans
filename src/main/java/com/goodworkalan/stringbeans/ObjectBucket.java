package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

public class ObjectBucket {
    private final Type propertyType;

    private final String name;

    private final Object value;

    public ObjectBucket(Type propertyType, String name, Object value) {
        this.propertyType = propertyType;
        this.name = name;
        this.value = value;
    }

    public Type getPropertyType() {
        return propertyType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
