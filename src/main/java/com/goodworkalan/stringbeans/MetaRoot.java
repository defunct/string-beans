package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;
import java.util.Collections;

import com.goodworkalan.reflective.Constructor;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.ReflectiveFactory;
import com.goodworkalan.stash.Stash;

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
    
    public Type getPropertyType(String name) {
        return constructor.getNative().getDeclaringClass();
    }
    
    public boolean isScalar() {
        return false;
    }
    
    public Object newStackInstance() {
        try {
            return constructor.newInstance();
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaRoot.class, "newInstance", e);
        }
    }
    
    public void set(Object object, String name, Object value) {
        ((Object[]) object)[0] = constructor.getNative().getDeclaringClass().cast(value);
    }

    /**
     * Returns the given <code>object</code> since we returned an object of the
     * appropriate type from {@link #newStackInstance() newStackInstance}.
     * 
     * @param stash
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param object
     *            The object taken from the stack.
     * @return The given object.
     */
    public Object newInstance(Stash stash, Object object) {
        return object;
    }
}
