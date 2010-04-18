package com.goodworkalan.stringbeans;

import java.util.HashMap;
import java.util.Map;

import com.goodworkalan.utility.ClassAssociation;

public class StringerBuilder {
    /** The set of beans that can be read or written. */
    private final ClassAssociation<Boolean> beans = new ClassAssociation<Boolean>();

    private final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public StringerBuilder() {
    }
    
    public StringerBuilder bean(Class<?> type) {
        beans.exact(type, Boolean.TRUE);
        return this;
    }
    
    public StringerBuilder beanSuperType(Class<?> type) {
        beans.derived(type, Boolean.TRUE);
        return this;
    }
    
    public StringerBuilder converter(Class<?> type, Converter converter) {
        converters.put(type, converter);
        return this;
    }
    
    public Stringer getInstance() {
        beans.derived(Object.class, Boolean.FALSE);
        return new Stringer(beans, new ConverterMap(converters));
    }
}
