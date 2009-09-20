package com.goodworkalan.stringbeans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.goodworkalan.reflective.Constructor;
import com.goodworkalan.reflective.Field;
import com.goodworkalan.reflective.Method;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.ReflectiveFactory;

public class MetaBean implements MetaObject {
    private final Type type;
    private final Class<?> objectClass;
    private final Constructor<?> constructor;
    private final Set<String> names = new HashSet<String>();
    private final Map<String, Method> writers = new HashMap<String, Method>();

    private final Map<String, Method> readers = new HashMap<String, Method>();
    private final Map<String, Field> fields = new HashMap<String, Field>();

    public MetaBean(Class<?> object) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(object, Object.class);
        } catch (IntrospectionException e) {
            throw new StringBeanException(0, e);
        }
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            java.lang.reflect.Method reader = property.getReadMethod();
            java.lang.reflect.Method writer = property.getWriteMethod();
            if (!(reader == null || writer == null)) {
                names.add(property.getName());
                writers.put(property.getName(), new Method(writer));
                readers.put(property.getName(), new Method(reader));
            }
        }
        for (java.lang.reflect.Field field : object.getFields()) {
            if (Modifier.isPublic(field.getModifiers()) && !names.contains(field.getName())) {
                names.add(field.getName());
                fields.put(field.getName(), new Field(field));
            }
        }
        try {
            this.constructor = new ReflectiveFactory().getConstructor(object);
        } catch (ReflectiveException e) {
            throw new StringBeanException(0, e);
        }
        this.type = object;
        this.objectClass = object;
    }

    public Object newInstance() {
        try {
            return constructor.newInstance();
        } catch (ReflectiveException e) {
            throw new StringBeanException(0, e);
        }
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public Type getType() {
        return type;
    }

    public Iterable<ObjectBucket> buckets(final Object object) {
        final Iterator<String> eachName = names.iterator();
        return new Iterable<ObjectBucket>() {
            public Iterator<ObjectBucket> iterator() {
                return new Iterator<ObjectBucket>() {
                    public boolean hasNext() {
                        return eachName.hasNext();
                    }

                    public ObjectBucket next() {
                        String name = eachName.next();
                        try {
                            Field field = fields.get(name);
                            if (field == null) {
                                Method writer = writers.get(name);
                                return new ObjectBucket(writer.getNative().getReturnType(), name, writer.invoke(object));
                            }
                            return new ObjectBucket(field.getNative().getType(),name, field.get(object));
                        } catch (ReflectiveException e) {
                            throw new StringBeanException(0, e);
                        }
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(names);
    }

    public Class<?> getPropertyClass(String name) {
        Field field = fields.get(name);
        if (field == null) {
            Method reader = readers.get(name);
            return reader.getNative().getReturnType();
        }
        return field.getNative().getType();
    }

    public Type getPropertyType(String name) {
        Field field = fields.get(name);
        if (field == null) {
            Method reader = readers.get(name);
            return reader.getNative().getGenericReturnType();
        }
        return field.getNative().getGenericType();
    }

    public void set(Object object, String name, Object value) {
        try {
            Field field = fields.get(name);
            if (field == null) {
                writers.get(name).invoke(object, value);
            } else {
                field.set(object, value);
            }
        } catch (ReflectiveException e) {
            throw new StringBeanException(0, e);
        }
    }

    public Object get(Object object, String name) {
        try {
            Field field = fields.get(name);
            if (field == null) {
                return writers.get(name).invoke(object);
            }
            return field.get(object);
        } catch (ReflectiveException e) {
            throw new StringBeanException(0, e);
        }
    }

    public boolean isScalar() {
        return false;
    }
}
