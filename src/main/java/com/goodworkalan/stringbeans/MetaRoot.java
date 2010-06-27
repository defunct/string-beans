package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

import com.goodworkalan.reflective.Reflective;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.stash.Stash;

/**
 * Performs the basic operations of serialization on a pseudo-root object that
 * has an actual root object as its only property.
 * 
 * @param <T>
 *            The root type of an object graph.
 * @author Alan Gutierrez
 */
public class MetaRoot<T> implements MetaObject {
    /** The root class of an object graph. */
    private final Class<T> rootClass;

    /**
     * Create a meta root using the given root class.
     * 
     * @param rootClass
     *            The root class of an object graph.
     */
    public MetaRoot(Class<T> rootClass) {
        this.rootClass = rootClass;
    }

    /**
     * Throw an <code>UnsupportedOperationException</code> since there is no
     * real object class, the root is the property of nothing.
     * 
     * @return The object class.
     */
    public Class<?> getObjectClass() {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the root class as the property type. The name is ignored.
     * 
     * @param The
     *            property name.
     * @return The root class of the object graph.
     */
    public Type getPropertyType(String name) {
        return rootClass;
    }

    /**
     * Return false indicating that the root of the object graph is not a
     * scalar.
     * 
     * @return False indicating that the root of the object graph is not a
     *         scalar.
     */
    public boolean isScalar() {
        return false;
    }
    
    // FIXME Is this ever called?
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

    /**
     * Record the root object in an object array.
     * 
     * @param object
     *            The object array.
     * @param name
     *            Ignored.
     * @param The
     *            object value.
     */
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
