package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

import com.goodworkalan.stash.Stash;

public class CollectionParser {
    /** The conversion strategies. */
    private final Converter converter;

    /**
     * The heterogeneous container of unforeseen participants in the
     * construction of beans in the object graph.
     */
    private final Stash stash;
    
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
    public CollectionParser(Converter stringer, boolean ignoreMissing) {
        this.converter = stringer;
        this.stash = new Stash();
        this.ignoreMissing = ignoreMissing;
    }

    /**
     * Get the heterogeneous container of unforeseen participants in the
     * construction of beans in the object graph.
     * 
     * @return The heterogeneous container of participants.
     */
    public Stash getStash() {
        return stash;
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
            // What? Don't we set nulls?
        } else if (objectStack.isScalar()) {
            objectStack.pop(value.toString());
        } else {
            Object object = objectStack.getLastContainerPushed();
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
            if (objectStack.push(null, getClassName(value))) {
                pop(objectStack, value);
            }
        }
    }

    private void parseMap(ObjectStack objectStack, Map<Object, Object> map) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (objectStack.push(entry.getKey().toString(), getClassName(value))) {
                pop(objectStack, value);
            }
        }
    }
    
    public <T> void populate(T rootObject, Map<?, ?> map) {
        if (map != null) {
            MetaObject metaRoot = converter.getMetaObject(rootObject.getClass());
            if (metaRoot.isScalar()) {
                throw new IllegalStateException();
            }
            ObjectStack objectStack = new ObjectStack(converter, stash, metaRoot, rootObject, ignoreMissing);
            parseMap(objectStack, toObjectMap(map));
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
    public <T> T create(Class<T> rootClass, Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        Object[] bean = new Object[1];
        ObjectStack objectStack = new ObjectStack(converter, stash, new MetaRoot<T>(rootClass), bean, ignoreMissing);
        objectStack.push(null, null);
        parseMap(objectStack, toObjectMap(map));
        objectStack.pop();
        return rootClass.cast(bean[0]);
    }
}
