package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.ReflectiveFactory;

public class MetaMap implements MetaObject {
    private final ParameterizedType pt;
    
    private final ReflectiveFactory reflectiveFactory;

    public MetaMap(ReflectiveFactory reflectiveFactory, ParameterizedType pt) {
        this.reflectiveFactory =reflectiveFactory;
        this.pt = pt;
    }

    public Iterable<ObjectBucket> buckets(Object object) {
        final Iterator<?> eachEntry = ((Map<?, ?>) object).entrySet().iterator();
        return new Iterable<ObjectBucket>() {
            public Iterator<ObjectBucket> iterator() {
                return new Iterator<ObjectBucket>() {
                    public boolean hasNext() {
                        return eachEntry.hasNext();
                    }

                    public ObjectBucket next() {
                        Map.Entry<?, ?> entry = (Entry<?, ?>) eachEntry.next();
                        return new ObjectBucket(getPropertyType(null), entry.getKey().toString(), entry.getValue());
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Class<?> getObjectClass() {
        return (Class<?>) pt.getRawType();
    }

    public Type getPropertyType(String name) {
        return pt.getActualTypeArguments()[1];
    }

    public boolean isScalar() {
        return false;
    }

    public Object newInstance() {
        Class<?> mapClass = (Class<?>) pt.getRawType();
        if (!mapClass.isInterface()) {
            try {
                return reflectiveFactory.getConstructor(mapClass).newInstance();
            } catch (ReflectiveException e) {
                throw new StringBeanException(MetaMap.class, "newInstance", e);
            }
        } else if (SortedMap.class.isAssignableFrom(mapClass)) {
            return new TreeMap<Object, Object>();
        }
        return new LinkedHashMap<Object, Object>();
    }

    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Map<Object, Object>) object).put(name, value);
    }
}
