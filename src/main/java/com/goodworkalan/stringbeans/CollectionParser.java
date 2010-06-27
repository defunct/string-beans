package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a String Beans object graph from a diffused object graph.
 *
 * @author Alan Gutierrez
 */
public class CollectionParser {
    /** The conversion strategies. */
    private final Converter converter;

    /**
     * The heterogeneous container of unforeseen participants in the
     * construction of beans in the object graph.
     */
    private final Map<Object, Object> participants;
    
    /**
     * Whether to ignore properties pushed onto the stack for a Java Bean that
     * are not defined as members of the Java Bean.
     */
    private final boolean ignoreMissing;

    /**
     * Construct an object stack that builds an object graph starting at the
     * given <code>root</code> object which is maniuplated using the given
     * <code>rootMeta</code> object.
     * 
     * @param converter
     *            The String Beans configuration.
     * @param ignoreMissing
     *            Whether to ignore properties pushed onto the stack for a Java
     *            Bean that are not defined as members of the Java Bean.
     */
    public CollectionParser(Converter converter, boolean ignoreMissing) {
        this.converter = converter;
        this.participants = new HashMap<Object, Object>();
        this.ignoreMissing = ignoreMissing;
    }

    /**
     * Get the heterogeneous container of unforeseen participants in the
     * construction of beans in the object graph.
     * 
     * @return The heterogeneous container of participants.
     */
    public Map<Object, Object> getParticipants() {
        return participants;
    }

    /**
     * Cast an object to an object map. Extracted to a method to isolate the
     * warning suppression.
     * 
     * @param object
     *            The object.
     * @return The object cast to a map.
     */
    @SuppressWarnings("unchecked")
    private static Map<Object, Object> toObjectMap(Object object) {
        return (Map<Object, Object>) object;
    }
    
    /**
     * Cast an object to an object map. Extracted to a method to isolate the
     * warning suppression.
     * 
     * @param object
     *            The object.
     * @return The object cast to a map.
     */
    @SuppressWarnings("unchecked")
    private static Collection<Object> toObjectCollection(Object object) {
        return (Collection<Object>) object;
    }

    /**
     * Get the class name from the given object, if the object is map using the
     * map property, otherwise return null.
     * 
     * @param value
     *            The object.
     * @return The class name of the object.
     */
    private String getClassName(Object value) {
        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            Class<?> type = (Class<?>) map.get("class");
            if (type != null) {
                return type.getName();
            }
        }
        return null;
    }

    /**
     * Pop the object from the top object stack assigning it the given string
     * value representation if it is a scalar.
     * 
     * @param objectStack
     *            The object stack.
     * @param value
     *            The string value representation.
     */
    private void pop(ObjectStack objectStack, Object value) {
        if (value == null) {
            // FIXME What? Don't we set nulls?
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

    /**
     * Push elements of the given collection onto the object stack populating
     * the collection at the top of the object stack.
     * 
     * @param objectStack
     *            The object stack.
     * @param collection
     *            The collection to push.
     */
    private void parseCollection(ObjectStack objectStack, Collection<?> collection) {
        for (Object value : collection) {
            if (objectStack.push(null, getClassName(value))) {
                pop(objectStack, value);
            }
        }
    }

    /**
     * Push the entries of the given map onto the object stack populating the
     * map at the top of the object stack.
     * 
     * @param objectStack
     *            The object stack.
     * @param map
     *            The map to push.
     */
    private void parseMap(ObjectStack objectStack, Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (objectStack.push(entry.getKey().toString(), getClassName(value))) {
                pop(objectStack, value);
            }
        }
    }

    /**
     * Populate the given root object using the given diffused object graph.
     * 
     * @param <T>
     *            The type to populate.
     * @param rootObject
     *            The object to populate.
     * @param map
     *            The diffused object graph.
     */
    public <T> void populate(T rootObject, Map<?, ?> map) {
        if (map != null) {
            MetaObject metaRoot = converter.getMetaObject(rootObject.getClass());
            if (metaRoot.isScalar()) {
                throw new IllegalStateException();
            }
            ObjectStack objectStack = new ObjectStack(converter, participants, metaRoot, rootObject, ignoreMissing);
            parseMap(objectStack, map);
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
        ObjectStack objectStack = new ObjectStack(converter, participants, new MetaRoot<T>(rootClass), bean, ignoreMissing);
        objectStack.push(null, null);
        parseMap(objectStack, toObjectMap(map));
        objectStack.pop();
        return rootClass.cast(bean[0]);
    }
}
