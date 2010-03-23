package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;

public class ObjectStack {
    private final Stringer stringer;

    private final LinkedList<MetaObject> objectInfoStack = new LinkedList<MetaObject>();

    private Object lastPopped;

    private final LinkedList<Object> objectStack = new LinkedList<Object>();
    
    private final LinkedList<String> nameStack = new LinkedList<String>();

    public ObjectStack(Stringer stringer, MetaObject rootMeta, Object root) {
        this.stringer = stringer;
        this.objectInfoStack.addLast(rootMeta);
        this.objectStack.addLast(root);
    }
    
    private void push(String name, Class<?> objectClass) {
        MetaObject metaObject;
        Type propertyType = objectInfoStack.getLast().getPropertyType(name);
        if (objectClass == null) {
            metaObject = MetaObjects.getInstance(stringer, propertyType);
        } else {
            Class<?> propertyClass = (propertyType instanceof Class<?>) ? (Class<?>) propertyType : (Class<?>) ((ParameterizedType) propertyType).getRawType();
            if (!propertyClass.isAssignableFrom(objectClass)) {
                throw new StringBeanException(ObjectStack.class, "pushIsNotAssignableFrom");
            }
            metaObject = MetaObjects.getInstance(stringer, objectClass);
        }
        if (!metaObject.isScalar()) {
            Object newObject = metaObject.newInstance();
            objectInfoStack.getLast().set(objectStack.getLast(), name, newObject);
            objectStack.add(newObject);
        }
        nameStack.addLast(name);
        objectInfoStack.add(metaObject);
        lastPopped = null;
    }
 
    public void push(String name, String className) {
        if (className == null) {
            push(name, (Class<?>) null);
        } else {
            push(name, forName(className));
        }
    }

    public boolean isScalar() {
        return objectInfoStack.getLast().isScalar();
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
        MetaObject scalar = objectInfoStack.removeLast();
        String pushedName = nameStack.removeLast();
        Object value = stringer.getConverter().fromString(scalar.getObjectClass(), string);
        objectInfoStack.getLast().set(objectStack.getLast(), pushedName, value);
        lastPopped = value;
    }

    public void pop() {
        lastPopped = objectStack.removeLast();
        objectInfoStack.removeLast();
    }
    
    public Object getLastPopped() {
        return lastPopped;
    }
}
