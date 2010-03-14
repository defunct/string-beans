package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.Map;

import com.goodworkalan.reflective.ReflectiveFactory;

public class ObjectStack {
    private final Stringer stringer;

    private final LinkedList<MetaObject> objectInfoStack = new LinkedList<MetaObject>();

    private Object lastPopped;

    private final LinkedList<Object> objectStack = new LinkedList<Object>();
    
    private final LinkedList<String> nameStack = new LinkedList<String>();

    public ObjectStack(Stringer stringer) {
        this.stringer = stringer;
    }
    
    private void push(String name, Class<?> objectClass) {
        MetaObject metaObject;
        Class<?> propertyClass = objectInfoStack.getLast().getPropertyClass(name);
        if (objectClass == null) {
            metaObject = MetaObjects.getInstance(stringer, propertyClass);
        } else if (objectClass.equals(ClassNotAvailable.class) && stringer.hasSubClasses(propertyClass)) {
            metaObject = new MetaMap(new ReflectiveFactory(), (ParameterizedType) Dictionary.class.getGenericSuperclass());
        } else {
            if (!stringer.isSubClass(objectClass)) {
                throw new StringBeanException(ObjectStack.class, "pushIsNotSubClass");
            }
            if (!propertyClass.isAssignableFrom(objectClass)) {
                throw new StringBeanException(ObjectStack.class, "pushIsNotAssignableFrom");
            }
            metaObject = MetaObjects.getInstance(stringer, objectClass);
        }
        if (!metaObject.isScalar()) {
            Object newObject = metaObject.newInstance();
            if (!objectInfoStack.isEmpty()) {
                objectInfoStack.getLast().set(objectStack.getLast(), name, newObject);
            }
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

    private void createContainer(String name, MetaObject metaObject) {
        if (!metaObject.isScalar()) {
            Object newObject = metaObject.newInstance();
            if (!objectInfoStack.isEmpty()) {
                objectInfoStack.getLast().set(objectStack.getLast(), name, newObject);
            }
            objectStack.add(newObject);
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

    public void pop(String string) {
        MetaObject scalar = objectInfoStack.removeLast();
        Object value = stringer.getConverter().fromString(scalar.getObjectClass(), string);
        String pushedName = nameStack.removeLast();
        if (pushedName.equals("class")) {
            Class<?> objectClass = forName(pushedName);
            MetaObject metaObject = objectInfoStack.removeLast();
            Object object = objectStack.removeLast();
            if (object instanceof Dictionary) {
                Dictionary dictionary = (Dictionary) object;
                if (!stringer.isSubClass(objectClass)) {
                    throw new StringBeanException(ObjectStack.class, "popIsNotSubClass");
                }
                if (!metaObject.getObjectClass().isAssignableFrom(objectClass)) {
                    throw new StringBeanException(ObjectStack.class, "popIsNotAssignableFrom");
                } 
                createContainer(nameStack.getLast(), MetaObjects.getInstance(stringer, objectClass));
                metaObject = objectInfoStack.getLast();
                object = objectStack.getLast();
                for (Map.Entry<String, Object> entry : dictionary.entrySet()) {
                    metaObject.set(object, entry.getKey(), entry.getValue());
                }
            }
        } else if (!objectInfoStack.isEmpty()) {
            objectInfoStack.getLast().set(objectStack.getLast(), pushedName, value);
        }
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
