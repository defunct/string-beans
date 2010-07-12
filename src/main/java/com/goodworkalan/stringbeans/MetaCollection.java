package com.goodworkalan.stringbeans;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.goodworkalan.danger.Danger;

/**
 * Performs the basic operations of serialization on a collection type.
 * 
 * @author Alan Gutierrez
 */
public class MetaCollection implements MetaObject {
    /** The actualized parameterized collection type. */
    private final ParameterizedType collectionType;

    /**
     * Create an instance of collection meta information for the given
     * actualized parameterized collection type.
     * 
     * @param collectionType
     *            The actualized parameterized collection type.
     */
    public MetaCollection(ParameterizedType collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * Get the collection type.
     * 
     * @return The collection type.
     */
    public Class<?> getObjectClass() {
        return (Class<?>) collectionType.getRawType();
    }

    /**
     * Return false indicating that collections are not scalars.
     * 
     * @return False indicating that collections are not scalars.
     */
    public boolean isScalar() {
        return false;
    }

    /**
     * Add the given value to the end of the given collection object. The name
     * is ignored.
     * 
     * @param object
     *            The collection object.
     * @param name
     *            Ignored for collections.
     * @param value
     *            The value to add to the collection.
     */
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Collection) object).add(value);
    }

    /**
     * Create a new instance of the collection type to use to gather child
     * objects during parsing. If the collection type is a class a new instance
     * of the type is created using the default constructor, otherwise an array
     * list is created.
     * 
     * @return A new collection instance.
     */
    public Object newStackInstance() {
        Class<?> objectClass = getObjectClass();
        if (objectClass.isInterface() || Modifier.isAbstract(objectClass.getModifiers())) {
            if (List.class.isAssignableFrom(getObjectClass())) {
                objectClass = ArrayList.class;
            }
        }
        Class<?> createClass = objectClass;
        try {
            return createClass.newInstance();
        } catch (Exception e) {
            throw new Danger(e, MetaCollection.class, "newInstance");
        }
    }

    /**
     * Get the collection item type. The name is ignored.
     * 
     * @param name
     *            Ignored for collecitons.
     * @return The collection item type.
     */
    public Type getPropertyType(String name) {
        return collectionType.getActualTypeArguments()[0];
    }
    
    /**
     * Returns the given <code>object</code> since we returned a collection of
     * the appropriate type from {@link #newStackInstance() newStackInstance}.
     * 
     * @param participants
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param object
     *            The object taken from the stack.
     * @return The given object.
     */
    public Object newInstance(Map<Object, Object> participants, Object object) {
        return object;
    }
}