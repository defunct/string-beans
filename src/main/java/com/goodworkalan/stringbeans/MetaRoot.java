package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;
import java.util.Collections;

import com.goodworkalan.reflective.Reflective;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.stash.Stash;

// TODO Document.
public class MetaRoot<T> implements MetaObject {
    // TODO Document.
    private final Class<T> rootClass;
    
    // TODO Document.
    public MetaRoot(Class<T> rootClass) {
        this.rootClass = rootClass;
    }
    
    // TODO Document.
    public Iterable<ObjectBucket> buckets(Object object) {
        Object[] objects = (Object[]) object;
        return Collections.singletonList(new ObjectBucket(Object.class, null, objects[0]));
    }
    
    // TODO Document.
    public Class<?> getObjectClass() {
        throw new UnsupportedOperationException();
    }
    
    // TODO Document.
    public Type getPropertyType(String name) {
        return rootClass;
    }
    
    // TODO Document.
    public boolean isScalar() {
        return false;
    }
    
    // TODO Document.
    public Object newStackInstance() {
        try {
            try {
                return rootClass.newInstance();
            } catch (Throwable e) {
                throw new ReflectiveException(Reflective.encode(e), e);
            }
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaRoot.class, "newInstance", e);
        }
    }
    
    // TODO Document.
    public void set(Object object, String name, Object value) {
        ((Object[]) object)[0] = rootClass.cast(value);
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
