package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Represents the basic operations on objects during parsing and emission of
 * serialized beans; creating objects, reading and writing object properties,
 * and determining object and property types.
 * 
 * @author Alan Gutierrez
 */
public interface MetaObject {
    /**
     * Get class of object that this meta object manipulates.
     * 
     * @return The target object class.
     */
    public Class<?> getObjectClass();

    /**
     * Create a new instance, possibly a place holder instance, to place on the
     * object stack. Most implementations, including all of the default
     * implementations will simply return an instance of the object.
     * <p>
     * Advanced implementations may want to construct the actual object based on
     * the object properties. This method gives those implementations the
     * opportunity to place a map on the stack to gather the properties, then
     * build the actual object using the {@link #newInstance(Stash, Object)
     * newInstance} method.
     * 
     * @return A new instance, possibly a place holder instance, to place on the
     *         object stack.
     */
    public Object newStackInstance();

    /**
     * Construct a new instance of the actual object using the given
     * <code>object</code> taken from the stack. The given <code>stash</code>
     * can be used to provide the method with other objects necessary for
     * construction.
     * <p>
     * Most implementations, including all of the default implementations will
     * simply return the given <code>object</code> since it is an instance of
     * the actual object.
     * <p>
     * Advanced implementations may want to construct the actual object based on
     * the object properties. Those implementations can place a map on the stack
     * by returning one form the {@link #newStackInstance() newStackInstance}
     * method. That map will be passed to this method, which can build
     * 
     * @param participants
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param object
     *            The object taken from the stack.
     * @return A new instance of the actual object to be added to the
     *         constructed object graph.
     */
    public Object newInstance(Map<Object, Object> participants, Object object);

    /**
     * Return true if the object managed by this meta object is a scalar object,
     * where a scalar is a primitive or string.
     * 
     * @return True if the meta object manages scalar objects.
     */
    public boolean isScalar();

    /**
     * Set the given <code>value</code> on the property with the given
     * <code>name</code> on the given <code>object</code>.
     * <p>
     * The name is not always applicable. The default collection implementation
     * can ignores the name. It adds the value to end of the collection.
     * 
     * @param object
     *            The container object.
     * @param name
     *            The property name.
     * @param value
     *            The object value.
     */
    public void set(Object object, String name, Object value);
    
    // FIXME To support a set of types, this will have to take a name and a
    // class. Only if the set is a singleton set, can the class be null.
    public Type getPropertyType(String name);
}
