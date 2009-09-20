package com.goodworkalan.stringbeans;

import java.util.Collections;

public class MetaRoot implements MetaObject {
    public Iterable<ObjectBucket> buckets(Object object) {
        Object[] objects = (Object[]) object;
        return Collections.singletonList(new ObjectBucket(Object.class, null, objects[0]));
    }
    
    public Class<?> getObjectClass() {
        return (new Object[0]).getClass();
    }
    
    public Class<?> getPropertyClass(String name) {
        return Object.class;
    }
    
    public boolean isScalar() {
        return false;
    }
    
    public Object newInstance() {
        return new Object[1];
    }
    
    public void set(Object object, String name, Object value) {
        Object[] objects = (Object[]) object;
        objects[0] = value;
    }
}
