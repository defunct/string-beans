package com.goodworkalan.infuse;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Used to create collections from URL encoded form submissions that describe
 * integer indexed arrays. The values are assigned to the sorted map at the
 * given integer index. Because the resulting collection is the values
 * collection of the sorted map, the effect is to collapse the array, so that
 * unspecified indexes are ignored and the collection created is contiguous.
 * <p>
 * Any item placed at and index following one or more unspecified indexes will
 * not be located at the same index in the resulting collection.
 * 
 * @author Alan Gutierrez
 */
public class SparseCollection implements Collection<Object> {
    /** A tree map of integers to objects used to model a sparse list. */
    public final SortedMap<Integer, Object> map = new TreeMap<Integer, Object>();

    /**
     * Unsupported operation add, since this is a read only collection.
     * 
     * @param o
     *            The object to add.
     * @return True if the collection changed as a result of the operation.
     */
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation addAll, since this is a read only collection.
     * 
     * @param o
     *            The collection to add.
     * @return True if the collection changed as a result of the operation.
     */
    public boolean addAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return true if this collection contains the given value.
     * 
     * @param o
     *            The element to test.
     * @return True if this collection contains the given element.
     */
    public boolean contains(Object o) {
        return map.containsValue(o);
    }

    /**
     * Return true if this collection contains add of the values in the given
     * collection.
     * 
     * @param o
     *            The collection to test.
     * @return True if this collection contains all of the values in the given
     *         collection.
     */
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!map.containsValue(o)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an array of containing all of the collection elements.
     * 
     * @return An array contianing all of the collection elements.
     */
    public Object[] toArray() {
        return map.values().toArray();
    }

    /**
     * Returns an array containing all of the collection elements. The type of the returned
     * array is the runtime type of the given array. If the given array is to
     * small or to large to contain the collection exactly, a new array is
     * allocated, filled and returned. If the given array is exactly the same
     * size of the collection, it is filled and returned.
     * 
     * @param a
     *            The array to use.
     * @return The given array filled with the collection elements if it is
     *         exactly the same size as the collection or a new array of the
     *         same type that is filled with the collection elements.
     */
    public <T extends Object> T[] toArray(T[] a) {
        return map.values().toArray(a);
    }
    
    /**
     * Returns the count of elements in this collection.
     * 
     * @return The size of the collection.
     */
    public int size() {
        return map.size();
    }

    /**
     * Return true if there are no elements in this collection.
     * 
     * @return True if the collection is empty.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    /**
     * Returns an iterator over the elements in this collection. The elements
     * returned are in the order specified by the indexes of the associated
     * sorted map. By using the values collection of the sorted map, we skip the
     * missing elements in the map of integers to objects.
     * 
     * @return An iterator over the elements in the collection.
     */
    public Iterator<Object> iterator() {
        return map.values().iterator();
    }

    /**
     * Unsupported operation remove, since this is a read only collection.
     * 
     * @param o
     *            The object to remove.
     * @return True if the collection changed as a result of the operation.
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation removeAll, since this is a read only collection.
     * 
     * @param o
     *            The collection to remove.
     * @return True if the collection changed as a result of the operation.
     */
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation retain, since this is a read only collection.
     * 
     * @param o
     *            The collection to retain.
     * @return True if the collection changed as a result of the operation.
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation clear, since this is a read only collection.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
