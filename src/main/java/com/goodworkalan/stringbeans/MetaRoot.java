package com.goodworkalan.stringbeans;

import java.util.Collections;

import com.goodworkalan.reflective.Constructor;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.ReflectiveFactory;

public class MetaRoot<T> implements MetaObject {
    private final Constructor<T> constructor;
    
    public MetaRoot(Class<T> rootClass) {
        try {
            this.constructor = new ReflectiveFactory().getConstructor(rootClass);
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaRoot.class, "getConstructor", e);
        }
    }
    
    public Iterable<ObjectBucket> buckets(Object object) {
        Object[] objects = (Object[]) object;
        return Collections.singletonList(new ObjectBucket(Object.class, null, objects[0]));
    }
    
    public Class<?> getObjectClass() {
        throw new UnsupportedOperationException();
    }
    
    public Class<?> getPropertyClass(String name) {
        return constructor.getNative().getDeclaringClass();
    }
    
    public boolean isScalar() {
        return false;
    }
    
    public Object newInstance() {
        try {
            return constructor.newInstance();
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaRoot.class, "newInstance", e);
        }
    }
    
    public void set(Object object, String name, Object value) {
        ((Object[]) object)[0] = constructor.getNative().getDeclaringClass().cast(value);
    }
}
