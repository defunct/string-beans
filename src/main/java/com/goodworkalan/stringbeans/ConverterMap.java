package com.goodworkalan.stringbeans;

import java.util.HashMap;
import java.util.Map;

public class ConverterMap extends Converter {
    private final Converter defaultConverter;

    private final Map<Class<?>, Converter> map;

    public ConverterMap(Map<Class<?>, Converter> map, Converter defaultConverter) {
        this.map = new HashMap<Class<?>, Converter>(map);
        this.defaultConverter = defaultConverter;
    }

    public ConverterMap(Map<Class<?>, Converter> map) {
        this(map, new Converter());
    }

    public Object fromString(Class<?> type, String string) {
        return getConverter(type).fromString(type, string);
    }

    @Override
    public String toString(Object object) {
        if (object == null) {
            return null;
        }
        return getConverter(object.getClass()).toString(object);
    }

    private Converter getConverter(Class<?> type) {
        Converter converter = map.get(type);
        if (converter == null) {
            converter = defaultConverter;
        }
        return converter;
    }
}
