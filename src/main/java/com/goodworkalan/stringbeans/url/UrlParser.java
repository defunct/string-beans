package com.goodworkalan.stringbeans.url;

import java.util.Map;

import com.goodworkalan.permeate.ParseException;
import com.goodworkalan.permeate.Part;
import com.goodworkalan.permeate.Path;
import com.goodworkalan.stringbeans.CollectionParser;
import com.goodworkalan.stringbeans.Converter;
import com.goodworkalan.stringbeans.StringBeanException;

/**
 * Create a String Beans object graph by extracting values from a map of
 * Permeate paths to string values that could be provided via a URL query
 * string.
 * 
 * @author Alan Gutierrez
 */
public class UrlParser {
    /** The underlying collection parser. */
    private final CollectionParser collectionParser;
    
    /**
     * Create a URL parser.
     * 
     * @param converter
     *            The String Beans configuration.
     */
    public UrlParser(Converter converter) {
        this.collectionParser = new CollectionParser(converter, true);
    }

    /**
     * Convert the map of object graph path strings to values into a a diffused
     * object graph.
     * 
     * @param form
     *            The map f object graph path strings to values.
     * @return A diffused object graph.
     */
    private StringToObjectMap tree(Map<String, String> form) {
        StringToObjectMap tree = new StringToObjectMap(); 
        for (Map.Entry<String, String> entry : form.entrySet()) {
            try {
                Path path = new Path(entry.getKey(), false);
                set(path, 0, tree, entry.getValue());
            } catch (ParseException e) {
            }
        }
        return tree;
    }

    /**
     * Get the heterogeneous container of unforeseen participants in the
     * construction of beans in the object graph.
     * 
     * @return The heterogeneous container of participants.
     */
    public Map<Object, Object> getParticipants() {
        return collectionParser.getParticipants();
    }

    /**
     * Populate the root object with the object graph defined by the given map
     * of object path strings to values.
     * 
     * @param <T>
     *            The type of the object graph root.
     * @param rootObject
     *            The object graph root.
     * @param form
     *            The map of object path strings to values.
     */
    public <T> void populate(T rootObject, Map<String, String> form) {
        collectionParser.populate(rootObject, tree(form));
    }

    /**
     * Create an instance of the class with the object graph defined by the
     * given map of object path strings to values.
     * 
     * @param <T>
     *            The type of the object graph root.
     * @param rootClass
     *            The class of the object graph root.
     * @param form
     *            The map of object path strings to values.
     */
    public <T> T create(Class<T> rootClass, Map<String, String> form) {
        return collectionParser.create(rootClass, tree(form));
    }

    /**
     * Assign the value to the the container at the property index indicated by
     * the part within the path at the given index.
     * 
     * @param path
     *            The object graph path.
     * @param partIndex
     *            The index to the current part of the object graph path.
     * @param container
     *            The container.
     * @param value
     *            The value to assign to the object graph path.
     */
    private static void set(Path path, int partIndex, Object container, Object value) {
        Part part = path.get(partIndex);
        if (part.isInteger()) {
              put(path, partIndex, ((SparseCollection) container).map, new Integer(part.getName()), value);
        } else {
            put(path, partIndex, (StringToObjectMap) container, part.getName(), value);
        }
    }

    /**
     * Create a new container to assign to the property index indicated by the
     * part in the path at the part index. If the part index is an integer, a
     * sparse collection is created.
     * 
     * @param path
     *            The object graph path.
     * @param partIndex
     *            The index to the current part of the object graph path.
     * @return The object container.
     */
    private static Object newContainer(Path path, int partIndex) {
        return path.get(partIndex).isInteger() ? new SparseCollection() : new StringToObjectMap();
    }

    /**
     * Assert that the container at the property index indicated by the path and
     * part index is the correct type for the property index, that is, assert
     * that it is a sparse collection if the property index is an integer.
     * 
     * @param path
     *            The object graph path.
     * @param partIndex
     *            The index to the current part of the object graph path.
     * @param container
     *            The container.
     */
    private static void assertContainerType(Path path, int partIndex, Object container) {
        Class<?> containerClass = path.get(partIndex).isInteger() ? SparseCollection.class : StringToObjectMap.class;
        if (!containerClass.isAssignableFrom(container.getClass())) {
            throw new StringBeanException(UrlParser.class, "containerType", containerClass, container.getClass());
        }
    }

    /**
     * If the part index indicates the last part in the path, set the value to
     * the property index indicated by the part, otherwise add a container to
     * the property index if it doesn't already exist and continue down the
     * path.
     * 
     * @param <K>
     *            The type of the map key.
     * @param path
     *            The object graph path.
     * @param partIndex
     *            The part index.
     * @param map
     *            The map.
     * @param key
     *            The map key.
     * @param value
     *            The value to assign to the object graph path.
     */
    private static <K> void put(Path path, int partIndex, Map<K, Object> map, K key, Object value) {
        Part part = path.get(partIndex);
        if (partIndex == path.size() - 1) {
            if (!map.containsKey(part.getName())) {
                map.put(key, value);
            }
        } else {
            Object current = map.get(key);
            if (current == null) {
                current = newContainer(path, partIndex + 1);
                map.put(key, current);
            }
            assertContainerType(path, partIndex + 1, current);
            set(path, partIndex + 1, current, value);
        }
    }
}
