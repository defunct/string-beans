package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractEmitter {
    private final Stringer stringer;
    
    public AbstractEmitter(Stringer stringer) {
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
            // FIXME See http://bigeasy.lighthouseapp.com/projects/45005/tickets/18
            if (object instanceof Class<?>) {
                object = ((Class<?>) object).getCanonicalName();
            }
            emitScalar(object.getClass(), stringer.toString(object));
        }
    }
}
