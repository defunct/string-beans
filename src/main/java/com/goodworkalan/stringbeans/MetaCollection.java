package com.goodworkalan.stringbeans;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.goodworkalan.reflective.Reflective;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.stash.Stash;

// TODO Document.
public class MetaCollection implements MetaObject {
    // TODO Document.
    private final ParameterizedType collectionType;
    
    // TODO Document.
    public MetaCollection(ParameterizedType collectionType) {
        this.collectionType = collectionType;
    }

    // TODO Document.
    public Class<?> getObjectClass() {
        return (Class<?>) collectionType.getRawType();
    }
    
    // TODO Document.
    public boolean isScalar() {
        return false;
    }
    
    // TODO Document.
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Collection) object).add(value);
    }
    
    // TODO Document.
    public Object newStackInstance() {
        Class<?> objectClass = getObjectClass();
        if (objectClass.isInterface() || Modifier.isAbstract(objectClass.getModifiers())) {
            if (List.class.isAssignableFrom(getObjectClass())) {
                objectClass = ArrayList.class;
            }
        }
        Class<?> createClass = objectClass;
        try {
            try {
                return createClass.newInstance();
            } catch (Throwable e) {
                throw new ReflectiveException(Reflective.encode(e), e);
            }
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaCollection.class, "newInstance", e);
        }
    }
    
    // TODO Document.
    public Type getPropertyType(String name) {
        return collectionType.getActualTypeArguments()[0];
    }
    
    // TODO Document.
    public Iterable<ObjectBucket> buckets(Object object) {
        return null;
    }
    
    /**
     * Returns the given <code>object</code> since we returned a collection of
     * the appropriate type from {@link #newStackInstance() newStackInstance}.
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