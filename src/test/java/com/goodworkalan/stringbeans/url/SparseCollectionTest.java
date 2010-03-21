package com.goodworkalan.stringbeans.url;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.testng.annotations.Test;

import com.goodworkalan.stringbeans.url.SparseCollection;

/**
 * Unit tests for {@link SparseCollection}.
 * 
 * @author Alan Gutierrez
 */
public class SparseCollectionTest {
    /** Test that add raises an unsupported operation exception. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void add() {
        new SparseCollection().add(new Object());
    }

    /** Test that add raises an unsupported operation exception. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void addAll() {
        new SparseCollection().addAll(Collections.emptyList());
    }

    /** Test the contains delegate method. */
    @Test
    public void contains() {
        SparseCollection sparse = new SparseCollection();
        sparse.map.put(0, 1);
        assertTrue(sparse.contains(1));
    }

    /** Test the toArray delegate method. */
    @Test
    public void containsAll() {
        SparseCollection sparse = new SparseCollection();
        sparse.map.put(0, 1);
        sparse.map.put(3, 5);
        sparse.map.put(10, 102);
        assertTrue(sparse.containsAll(Arrays.asList(1, 5)));
        assertFalse(sparse.containsAll(Arrays.asList(3)));
    }

    /** Test the toArray delegate method. */
    @Test
    public void toArray() {
        SparseCollection sparse = new SparseCollection();
        sparse.map.put(0, 1);
        sparse.map.put(3, 5);
        sparse.map.put(10, 102);
        assertEquals(Arrays.asList(sparse.toArray()), Arrays.<Object> asList(1, 5, 102));
    }
    
    /** Test the typed toArray delegate method. */
    @Test
    public void toArrayTyped() {
        SparseCollection sparse = new SparseCollection();
        sparse.map.put(0, 1);
        sparse.map.put(3, 5);
        sparse.map.put(10, 102);
        assertEquals(Arrays.asList(sparse.toArray(new Integer[3])), Arrays.<Integer> asList(1, 5, 102));
    }
    
    /** Test the size delegate method. */
    @Test
    public void size() {
        assertEquals(new SparseCollection().size(), 0);
    }
    
    /** Test the isEmpty delegate method. */
    @Test
    public void isEmpty() {
        assertTrue(new SparseCollection().isEmpty());
    }
    
    /**  Test the iterator delegate method. */
    @Test
    public void iterator() {
        SparseCollection sparse = new SparseCollection();
        sparse.map.put(10, 102);
        sparse.map.put(0, 1);
        sparse.map.put(3, 5);
        Iterator<Object> iterator = sparse.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), 1);
        assertEquals(iterator.next(), 5);
        assertEquals(iterator.next(), 102);
        assertFalse(iterator.hasNext());
    }
    
    /** Test that remove raises an unsupported operation exception. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void remove() {
        new SparseCollection().remove(new Object());
    }

    
    /** Test that removeAll raises an unsupported operation exception. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void removeAll() {
        new SparseCollection().removeAll(Collections.emptyList());
    }
    
    /** Test that retainAll raises an unsupported operation exception. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void retainAll() {
        new SparseCollection().retainAll(Collections.emptyList());
    }
    
    /** Test that clear raises an unsupported operation exception. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void clear() {
        new SparseCollection().clear();
    }
}
