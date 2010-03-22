package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

public class CollectionParser {
    private final Stringer stringer;
    
    public CollectionParser(Stringer stringer) {
        this.stringer = stringer;
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> toObjectMap(Object map) {
        return (Map<Object, Object>) map;
    }
    
    @SuppressWarnings("unchecked")
    private static Collection<Object> toObjectCollection(Object list) {
        return (Collection<Object>) list;
    }
    
    private String getClassName(Object value) {
        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            map.get("class");
        }
        return null;
    }
    
    private void pop(ObjectStack objectStack, Object value) {
        if (value == null) {
        } else if (objectStack.isScalar()) {
            objectStack.pop(value.toString());
        } else {
            Object object = objectStack.getTop();
            if (object instanceof Collection<?>) {
                if (value instanceof Collection<?>) {
                    parseCollection(objectStack, toObjectCollection(value));
                } else {
                    throw new IllegalStateException();
                }
            } else {
                if (value instanceof Map<?, ?>) {
                    parseMap(objectStack, toObjectMap(value));
                } else {
                    throw new IllegalStateException();
                }
            }
            objectStack.pop();
        }
    }
    private void parseCollection(ObjectStack objectStack, Collection<Object> collection) {
        for (Object value : collection) {
            objectStack.push(null, getClassName(value));
            pop(objectStack, value);
        }
    }

    private void parseMap(ObjectStack objectStack, Map<Object, Object> map) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            objectStack.push(entry.getKey().toString(), getClassName(value));
            pop(objectStack, value);
        }
    }

    /**
     * Convert the given map into an object graph with the given root class.
     * 
     * @param <T>
     *            The type of the root object in the object graph.
     * @param rootClass
     *            The class of the root object in the object graph.
     * @param map
     *            The map to convert.
     * @return An instance of the root class.
     */
    public <T> T parse(Class<T> rootClass, Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        Object[] bean = new Object[1];
        ObjectStack objectStack = new ObjectStack(stringer, new MetaRoot<T>(rootClass), bean);
        objectStack.push(null, null);
        parseMap(objectStack, toObjectMap(map));
        objectStack.pop();
        return rootClass.cast(bean[0]);
    }
}
