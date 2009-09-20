package com.goodworkalan.stringbeans;

public class StringConverter extends Converter {
    @Override
    public Object fromString(Class<?> type, String string) {
        return string;
    }
}
