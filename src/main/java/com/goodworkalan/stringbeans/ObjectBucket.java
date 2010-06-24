package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

// TODO Document.
public class ObjectBucket {
    // TODO Document.
    private final Type propertyType;

    // TODO Document.
    private final String name;

    // TODO Document.
    private final Object value;

    // TODO Document.
    public ObjectBucket(Type propertyType, String name, Object value) {
        this.propertyType = propertyType;
        this.name = name;
        this.value = value;
    }

    // TODO Document.
    public Type getPropertyType() {
        return propertyType;
    }

    // TODO Document.
    public String getName() {
        return name;
    }

    // TODO Document.
    public Object getValue() {
        return value;
    }
}
