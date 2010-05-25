package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;

import com.goodworkalan.stash.Stash;

public class ObjectStack {
    private final Converter stringer;

    /**
     * A heterogeneous container of unforeseen participants in the construction
     * of the object.
     */
    private final Stash stash;

    /** The stack of meta objects. */
    private final LinkedList<MetaObject> metaObjectStack = new LinkedList<MetaObject>();

    /** The last object popped from the object stack. */
    private Object lastPopped;

    /** The stack of objects. */
    private final LinkedList<Object> objectStack = new LinkedList<Object>();
    
    /** The stack of names. */
    private final LinkedList<String> nameStack = new LinkedList<String>();
    
    /**
     * Whether to ignore names that are missing in the object currently being
     * populated by the object stack.
     */
    private final boolean ignoreMissing;

    /**
     * Construct an object stack that builds an object graph starting at the
     * given <code>root</code> object which is maniuplated using the given
     * <code>rootMeta</code> object.
     * 
     * @param stringer
     * @param stash
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param rootMeta
     * @param root
     * @param ignoreMissing
     */
    public ObjectStack(Converter stringer, Stash stash, MetaObject rootMeta, Object root, boolean ignoreMissing) {
        this.stringer = stringer;
        this.stash = stash;
        this.metaObjectStack.addLast(rootMeta);
        this.objectStack.addLast(root);
        this.ignoreMissing = ignoreMissing;
    }
    
    private boolean push(String name, Class<?> objectClass) {
        MetaObject metaObject;
        Type propertyType = metaObjectStack.getLast().getPropertyType(name);
        if (propertyType == null) {
            if (ignoreMissing) {
                return false;
            }
            throw new StringBeanException(ObjectStack.class, "doesNotExist");
        } 
        if (objectClass == null) {
            metaObject = stringer.getMetaObject(propertyType);
        } else {
            Class<?> propertyClass = (propertyType instanceof Class<?>) ? (Class<?>) propertyType : (Class<?>) ((ParameterizedType) propertyType).getRawType();
            if (!propertyClass.isAssignableFrom(objectClass)) {
                throw new StringBeanException(ObjectStack.class, "pushIsNotAssignableFrom");
            }
            metaObject = stringer.getMetaObject(objectClass);
        }
        if (!metaObject.isScalar()) {
            Object newObject = metaObject.newStackInstance();
            objectStack.add(newObject);
        }
        nameStack.addLast(name);
        metaObjectStack.add(metaObject);
        lastPopped = null;
        return true;
    }
 
    public boolean push(String name, String className) {
        if (className == null) {
            return push(name, (Class<?>) null);
        }
        return push(name, forName(className));
    }

    public boolean isScalar() {
        return metaObjectStack.getLast().isScalar();
    }

    Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new StringBeanException(ObjectStack.class, "forName", e);
        }
    }
    
    public Object getTop() {
        return objectStack.getLast();
    }

    public void pop(String string) {
        MetaObject scalar = metaObjectStack.removeLast();
        String pushedName = nameStack.removeLast();
        Object value = stringer.fromString(scalar.getObjectClass(), string);
        metaObjectStack.getLast().set(objectStack.getLast(), pushedName, value);
        lastPopped = value;
    }

    public void pop() {
        MetaObject metaObject = metaObjectStack.removeLast();
        lastPopped = metaObject.newInstance(stash, objectStack.removeLast());
        metaObjectStack.getLast().set(objectStack.getLast(), nameStack.removeLast(), lastPopped);
    }
    
    public Object getLastPopped() {
        return lastPopped;
    }
}
