package com.goodworkalan.stringbeans;

import java.util.Collection;
import java.util.Map;

public abstract class Emitter {
    private final Stringer stringer;
    
    public Emitter(Stringer stringer) {
        this.stringer = stringer;
    }

    protected abstract void beginDocument();

    protected abstract void emitNull();

    protected abstract void emitScalar(Converter converter, Class<?> type, Object object);

    protected abstract void emitSeries(Class<?> type, Collection<?> collection);

    protected abstract void emitDictionary(Class<?> type, Map<?, ?> map);

    protected abstract void emitBean(String alias, Class<?> type, MetaObject metaObject, Object object);

    protected abstract void endDocument();

    protected void expandObject(Object object) {
        if (object == null) {
            emitNull();
        } else if (Map.class.isAssignableFrom(object.getClass())) {
            emitDictionary(object.getClass(), (Map<?, ?>) object);
        } else if (Collection.class.isAssignableFrom(object.getClass())) {
            emitSeries(object.getClass(), (Collection<?>) object);
        } else if (stringer.isBean(object.getClass()))  {
            emitBean(stringer.getAlias(object.getClass()), object.getClass(), new MetaBean(object.getClass()), object);
        } else {
            emitScalar(stringer.getConverter(), object.getClass(), object);
        }
    }
    
    public void emit(Object object) {
        MetaBean objectInfo = new MetaBean(object.getClass());
        beginDocument();
        emitBean(stringer.getAlias(object.getClass()), Object.class, objectInfo, object);
        endDocument();
    }
}
