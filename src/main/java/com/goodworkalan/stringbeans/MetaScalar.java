package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

import com.goodworkalan.stash.Stash;
import com.goodworkalan.utility.Primitives;

/**
 * Performs the basic operations of serialization on a scalar type.
 * 
 * @author Alan Gutierrez
 */
public class MetaScalar implements MetaObject {
    /** The scalar type. */
    private final Class<?> objectClass;

    /**
     * Create a meta scalar for the given scalar type.
     * 
     * @param objectClass
     *            The scalar type.
     */
    public MetaScalar(Class<?> objectClass) {
        this.objectClass = Primitives.box(objectClass);
    }

    /**
     * Get the scalar object class.
     * 
     * @return The scalar object class.
     */
    public Class<?> getObjectClass() {
        return objectClass;
    }

    /**
     * Return true to indicate that this is a meta scalar.
     * 
     * @return True to indicate that is is a meta scalar.
     */
    public boolean isScalar() {
        return true;
    }

    /**
     * Throws an <code>UnsupportedOperationExpction</code> since scalars do not
     * have properties.
     * 
     * @param name
     *            The property name.
     * @return The property type.
     */
    public Type getPropertyType(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an <code>UnsupportedOperationExpction</code> since we do not need
     * a place holder instance to gather child members, because there are no
     * child members.
     * 
     * @return A new stack instance.
     */
    public Object newStackInstance() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an <code>UnsupportedOperationExpction</code> since there are no
     * properties.
     * 
     * @param object
     *            The container object.
     * @param name
     *            The property name.
     * @param value
     *            The property value.
     */
    public void set(Object object, String name, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an {@link UnsupportedOperationException} because new instances of
     * scalars are never created, they are only converted to and from primitives
     * or strings.
     * 
     * @param stash
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param object
     *            The object taken from the stack.
     * @return Nothing.
     * @exception UnsupportedOperationException
     *                If called.
     */
    public Object newInstance(Stash stash, Object object) {
        throw new UnsupportedOperationException();
    }
}
