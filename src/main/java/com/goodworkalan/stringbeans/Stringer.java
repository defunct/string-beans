package com.goodworkalan.stringbeans;

import java.util.Set;

public class Stringer {
    private final Set<Class<?>> beans;
    
    private final Converter converter;
    
    public Stringer(Set<Class<?>> beans, Converter converter) {
        this.beans = beans;
        this.converter = converter;
    }

    public boolean isBean(Class<?> type) {
        return beans.contains(type);
    }
    
    public Converter getConverter() {
        return converter;
    }
    
    public String getAlias(Class<?> type) {
        return "bean";
    }
    
    public boolean hasSubClasses(Class<?> type) {
        return false;
    }
    
    public boolean isSubClass(Class<?> objectClass) {
        return true;
    }
}
