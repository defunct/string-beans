package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

/**
 * Navigates a object tree and invokes an appropriate method to emit a an object
 * tree member. Derived classes can use this is a template to create an emitter
 * for a specific serial representation.
 * 
 * @author Alan Gutierrez
 */
public abstract class AbstractEmitter<O, X extends Exception> {
    /** The conversion configuration. */
    protected final Converter converter;
    
    /**
     * Create a new <code>AbstractEmitter</code> with the given conversion
     * definition.
     * 
     * @param converter
     *            The conversion definition.
     */
    public AbstractEmitter(Converter converter) {
        this.converter = converter;
    }

    /** Emit a null value. */
    protected abstract void emitNull(O output) throws X;

    /**
     * Emit a scalar object. Derived objects can use the <code>Diffuser</code>
     * defined in the <code>conversions</code> property.
     * 
     * @param object
     *            Type type of object.
     */
    protected abstract void emitScalar(O output, Object object) throws X;

    /**
     * Emit the given <code>collection</code> as a series of objects. Derived
     * objects can call the {@link #emitObject(Object) expandObject} method to
     * expand the collection elements.
     * 
     * @param collection
     *            The collection.
     */
    protected abstract void emitCollection(O output, Collection<?> collection) throws X;

    /**
     * Emit the given <code>map</code> as a series of name value pairs. Derived
     * objects can call the {@link #emitObject(Object) expandObject} method to
     * expand the map values.
     * 
     * @param map
     *            The map.
     */
    protected abstract void emitMap(O output, Map<?, ?> map) throws X;

    /**
     * Emit the given <code>bean</code> properties as as a series of name value
     * pairs. Derived objects can call the {@link #emitObject(Object)
     * expandObject} method to expand the property values.
     * 
     * @param metaObject
     *            The bean getters.
     * @param bean
     *            The bean.
     */
    protected abstract void emitBean(O output, MetaObject metaObject, Object bean) throws X;

    /**
     * Emit the given object calling the appropriate emit method for the object
     * type according to the configured conversions.
     * 
     * @param object
     *            The object.
     */
    protected void emitObject(O output, Object object) throws X {
        if (object == null) {
            emitNull(output);
        } else if (object instanceof Map<?, ?>) {
            emitMap(output, (Map<?, ?>) object);
        } else if (object instanceof Collection<?>) {
            emitCollection(output, (Collection<?>) object);
        } else if (converter.isBean(object.getClass()))  {
            emitBean(output, new MetaBean(object.getClass()), object);
        } else {
            emitScalar(output, object);
        }
    }
}
