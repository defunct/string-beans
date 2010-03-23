package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.reflective.ReflectiveFactory;

public class Converter {
    private final ReflectiveFactory reflectiveFactory;
    
    public Converter(ReflectiveFactory reflectiveFactory) {
        this.reflectiveFactory = reflectiveFactory;
    }
    
    /**
     * Convert a primitive type to an Object derived type, or return the given
     * type if it is already and Object derived type.
     * 
     * @param type
     *            The type to convert.
     * @return An Object derived type.
     */
    public static Class<?> box(Type type) {
        Class<?> raw =  (type instanceof Class<?>) ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType();
        if (raw.isPrimitive()) {
            if (long.class.isAssignableFrom(raw)) {
                return Long.class;
            } else if (int.class.isAssignableFrom(raw)) {
                return Integer.class;
            } else if (short.class.isAssignableFrom(raw)) {
                return Short.class;
            } else if (char.class.isAssignableFrom(raw)) {
                return Character.class;
            } else if (byte.class.isAssignableFrom(raw)) {
                return Byte.class;
            } else if (boolean.class.isAssignableFrom(raw)) {
                return Boolean.class;
            } else if (float.class.isAssignableFrom(raw)) {
                return Float.class;
            }
            return Double.class;
        }
        return raw;
    }
    
    public Converter(){
        this(new ReflectiveFactory());
    }
    
    public Object fromString(Class<?> type, String string) {
        if (string == null) {
            return null;
        }
        try {
            return reflectiveFactory.getConstructor(type, String.class).newInstance(string);
        } catch (ReflectiveException e) {
           throw new StringBeanException(Converter.class, "convert", e);
        }
    }

    public String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }
}
