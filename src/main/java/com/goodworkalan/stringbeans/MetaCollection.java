package com.goodworkalan.stringbeans;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.ReflectiveFactory;

public class MetaCollection implements MetaObject {
    private final ParameterizedType collectionType;
    
    public MetaCollection(ParameterizedType collectionType) {
        this.collectionType = collectionType;
    }

    public Class<?> getObjectClass() {
        return (Class<?>) collectionType.getRawType();
    }
    
    public boolean isScalar() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Collection) object).add(value);
    }
    
    public Object newInstance() {
        Class<?> objectClass = getObjectClass();
        if (objectClass.isInterface() || Modifier.isAbstract(objectClass.getModifiers())) {
            if (List.class.isAssignableFrom(getObjectClass())) {
                objectClass = ArrayList.class;
            }
        }
        try {
            return new ReflectiveFactory().getConstructor(objectClass).newInstance();
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaCollection.class, "newInstance");
        }
    }
    
    public Type getPropertyType(String name) {
        return collectionType.getActualTypeArguments()[0];
    }
    
    public Iterable<ObjectBucket> buckets(Object object) {
        return null;
    }
}