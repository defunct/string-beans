package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

/**
 * Navigates a object tree and invokes an appropriate method to emit a an object
 * tree member.
 * 
 * @author Alan Gutierrez
 */
public abstract class AbstractEmitter {
    /** The conversion configuration. */
    private final Converter stringer;
    
    /**
     * Create a new <code>AbstractEmitter</code> with the given conversion
     * definition.
     * 
     * @param stringer
     *            The conversion definition.
     */
    public AbstractEmitter(Converter stringer) {
        this.stringer = stringer;
    }

    protected abstract void emitNull();

    protected abstract void emitScalar(Class<?> type, Object object);

    protected abstract void emitSeries(Class<?> type, Collection<?> collection);

    protected abstract void emitDictionary(Class<?> type, Map<?, ?> map);

    protected abstract void emitBean(Class<?> type, MetaObject metaObject, Object object);

    protected void expandObject(Object object) {
        if (object == null) {
            emitNull();
        } else if (Map.class.isAssignableFrom(object.getClass())) {
            emitDictionary(object.getClass(), (Map<?, ?>) object);
        } else if (Collection.class.isAssignableFrom(object.getClass())) {
            emitSeries(object.getClass(), (Collection<?>) object);
        } else if (stringer.isBean(object.getClass()))  {
            emitBean(object.getClass(), new MetaBean(object.getClass()), object);
        } else {
            emitScalar(object.getClass(), stringer.toString(object));
        }
    }
}
