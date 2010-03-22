package com.goodworkalan.stringbeans;

public class ClassConverter extends Converter {
    @Override
    public Object fromString(Class<?> type, String string) {
        try {
            return Class.forName(string);
        } catch (ClassNotFoundException e) {
            throw new StringBeanException(ClassConverter.class, "classNotFound", string);
        }
    }
}
