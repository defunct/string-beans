package com.goodworkalan.stringbeans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.goodworkalan.reflective.Reflective;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.stash.Stash;

public class MetaBean implements BeanConstructor {
    private final Type type;
    private final Class<?> objectClass;
    private final Set<String> names = new HashSet<String>();
    
    private final Map<String, Method> writers = new HashMap<String, Method>();

    private final Map<String, Method> readers = new HashMap<String, Method>();
    private final Map<String, Field> fields = new HashMap<String, Field>();

    public MetaBean(Class<?> objectClass) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(objectClass);
        } catch (IntrospectionException e) {
            throw new StringBeanException(MetaBean.class, "getBeanInfo", e);
        }
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            java.lang.reflect.Method reader = property.getReadMethod();
            java.lang.reflect.Method writer = property.getWriteMethod();
            if (!(reader == null && writer == null)) {
                if (writer != null) {
                    writers.put(property.getName(), writer);
                }
                if (reader != null) {
                    names.add(property.getName());
                    readers.put(property.getName(), reader);
                }
            }
        }
        for (java.lang.reflect.Field field : objectClass.getFields()) {
            if (Modifier.isPublic(field.getModifiers()) && !names.contains(field.getName())) {
                names.add(field.getName());
                fields.put(field.getName(), field);
            }
        }
        this.type = objectClass;
        this.objectClass = objectClass;
    }

    public Object newStackInstance() {
        try {
            try {
                return objectClass.newInstance();
            } catch (Throwable e) {
                throw new ReflectiveException(Reflective.encode(e), e);
            }
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaBean.class, "newInstance", e);
        }
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public Type getType() {
        return type;
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(names);
    }

    public Class<?> getPropertyClass(String name) {
        Field field = fields.get(name);
        if (field == null) {
            Method reader = readers.get(name);
            return reader.getReturnType();
        }
        return field.getType();
    }

    public Type getPropertyType(String name) {
        Field field = fields.get(name);
        if (field == null) {
            Method reader = readers.get(name);
            if (reader == null) {
                Method writer = writers.get(name);
                if (writer == null) {
                    return null;
                }
                return writer.getGenericParameterTypes()[0];
            }
            return reader.getGenericReturnType();
        }
        return field.getGenericType();
    }

    public void set(Object object, String name, Object value) {
     try {
            try {
                Field field = fields.get(name);
                if (field == null) {
                    Method writer = writers.get(name);
                    if (writer != null) {
                        writer.invoke(object, value);
                    }
                } else {
                    field.set(object, value);
                }
            } catch (Throwable e) {
                throw new ReflectiveException(Reflective.encode(e), e);
            }
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaBean.class, "fieldSet", e);
        }
    }

    public Object get(Object object, String name) {
        try {
            try {
                Field field = fields.get(name);
                if (field == null) {
                    return readers.get(name).invoke(object);
                }
                return field.get(object);
            } catch (Throwable e) {
                throw new ReflectiveException(Reflective.encode(e), e);
            }
        } catch (ReflectiveException e) {
            throw new StringBeanException(MetaBean.class, "fieldGet", e);
        }
    }

    public boolean isScalar() {
        return false;
    }

    /**
     * Returns the given <code>object</code> since we returned a bean of the
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
