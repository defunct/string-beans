package com.goodworkalan.stringbeans;

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
    public static Class<?> box(Class<?> type) {
        if (type.isPrimitive()) {
            if (long.class.isAssignableFrom(type)) {
                return Long.class;
            } else if (int.class.isAssignableFrom(type)) {
                return Integer.class;
            } else if (short.class.isAssignableFrom(type)) {
                return Short.class;
            } else if (char.class.isAssignableFrom(type)) {
                return Character.class;
            } else if (byte.class.isAssignableFrom(type)) {
                return Byte.class;
            } else if (boolean.class.isAssignableFrom(type)) {
                return Boolean.class;
            } else if (float.class.isAssignableFrom(type)) {
                return Float.class;
            }
            return Double.class;
        }
        return type;
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
           throw new StringBeanException(0, e);
        }
    }

    public String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }
}
