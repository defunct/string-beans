package com.goodworkalan.stringbeans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringerBuilder {
    private final Set<Class<?>> beans = new HashSet<Class<?>>();

    private final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public StringerBuilder() {
    }
    
    public StringerBuilder bean(Class<?> type) {
        beans.add(type);
        return this;
    }
    
    public StringerBuilder converter(Class<?> type, Converter converter) {
        converters.put(type, converter);
        return this;
    }
    
    public Stringer getInstance() {
        return new Stringer(new HashSet<Class<?>>(beans), new ConverterMap(converters));
    }
}
