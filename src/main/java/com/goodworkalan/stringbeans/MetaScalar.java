package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;

import com.goodworkalan.stash.Stash;
import com.goodworkalan.utility.Primitives;

public class MetaScalar implements MetaObject {
    private final Class<?> objectClass;

    public MetaScalar(Class<?> objectClass) {
        this.objectClass = Primitives.box(objectClass);
    }

    public Iterable<ObjectBucket> buckets(Object object) {
        throw new UnsupportedOperationException();
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public boolean isScalar() {
        return true;
    }

    public Type getPropertyType(String name) {
        throw new UnsupportedOperationException();
    }

    public Object newStackInstance() {
        throw new UnsupportedOperationException();
    }

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
