package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

public class MetaObjects {
    public static MetaObject getInstance(Stringer stringer, Class<?> objectClass) {
        if (Map.class.isAssignableFrom(objectClass)) {
            
        } else if (Collection.class.isAssignableFrom(objectClass)) {
            
        } else if (stringer.isBean(objectClass)) {
            return new MetaBean(objectClass);
        }
        return new MetaScalar(objectClass);
    }
}
