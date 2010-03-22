package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class MetaObjects {
    public static MetaObject getInstance(Stringer stringer, Type objectType) {
        if (objectType instanceof ParameterizedType) {
            Class<?> objectClass = (Class<?>) ((ParameterizedType) objectType).getRawType();
            if (Map.class.isAssignableFrom(objectClass)) {
                
            } else if (Collection.class.isAssignableFrom(objectClass)) {
                return new MetaCollection((ParameterizedType) objectType);
            } else if (!Class.class.isAssignableFrom(objectClass)) {
                throw new IllegalStateException();
            }
            objectType = objectClass;
        } 
        if (objectType instanceof Class<?>) {
            Class<?> objectClass = (Class<?>) objectType;
            if (stringer.isBean(objectClass)) {
                return new MetaBean(objectClass);
            }
            return new MetaScalar(objectClass);
        }
        throw new IllegalStateException();
    }
}
