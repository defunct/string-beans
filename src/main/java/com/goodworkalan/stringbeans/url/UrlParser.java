package com.goodworkalan.stringbeans.url;

import java.util.Map;

import com.goodworkalan.permeate.ParseException;
import com.goodworkalan.permeate.Part;
import com.goodworkalan.permeate.Path;
import com.goodworkalan.stash.Stash;
import com.goodworkalan.stringbeans.CollectionParser;
import com.goodworkalan.stringbeans.StringBeanException;
import com.goodworkalan.stringbeans.Converter;

// TODO Document.
public class UrlParser {
    // TODO Document.
    private final CollectionParser collectionParser;
    
    // TODO Document.
    public UrlParser(Converter converter) {
        this.collectionParser = new CollectionParser(converter, true);
    }
    
    // TODO Document.
    private StringToObjectMap tree(Map<String, String> form) {
        StringToObjectMap tree = new StringToObjectMap(); 
        for (Map.Entry<String, String> entry : form.entrySet()) {
            try {
                Path path = new Path(entry.getKey(), false);
                set(path, tree, entry.getValue(), 0);
            } catch (ParseException e) {
            }
        }
        return tree;
    }

    // TODO Document.
    public Stash getStash() {
        return collectionParser.getStash();
    }
    
    // TODO Document.
    public <T> void populate(T rootObject, Map<String, String> form) {
        collectionParser.populate(rootObject, tree(form));
    }
    
    // TODO Document.
    public <T> T create(Class<T> rootClass, Map<String, String> form) {
        return collectionParser.create(rootClass, tree(form));
    }

    // TODO Document.
    private static boolean set(Path path, Object container, Object value, int index) {
        Part part = path.get(index);
        return part.isInteger()
             ? put(path, ((SparseCollection) container).map, new Integer(part.getName()), value, index)
             : put(path, (StringToObjectMap) container, part.getName(), value, index);
    }
    
    // TODO Document.
    private static Object newContainer(Path path, int partIndex) {
        return path.get(partIndex).isInteger() ? new SparseCollection() : new StringToObjectMap();
    }
    
    // TODO Document.
    private static void assertContainerType(Path path, int partIndex, Object container) {
        Class<?> containerClass = path.get(partIndex).isInteger() ? SparseCollection.class : StringToObjectMap.class;
        if (!containerClass.isAssignableFrom(container.getClass())) {
            throw new StringBeanException(UrlParser.class, "containerType", containerClass, container.getClass());
        }
    }
    
    // TODO Document.
    private static <K> boolean put(Path path, Map<K, Object> map, K key, Object value, int partIndex) {
        Part part = path.get(partIndex);
        if (partIndex == path.size() - 1) {
            if (!map.containsKey(part.getName())) {
                map.put(key, value);
                return true;
            }
            return false;
        }
        Object current = map.get(key);
        if (current == null) {
            current = newContainer(path, partIndex + 1);
            map.put(key, current);
        }
        assertContainerType(path, partIndex + 1, current);
        return set(path, current, value, partIndex + 1);
    }
}
