package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.goodworkalan.reflective.Reflective;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.utility.ClassAssociation;

/**
 * String beans emitter and parser configuration.
 *
 * @author Alan Gutierrez
 */
public class Stringer {
    /** The set of beans that can be read or written. */
    private final ClassAssociation<Class<? extends MetaObject>> beans;
    
    private final ConcurrentMap<Class<?>, MetaObject> metaObjectCache = new ConcurrentHashMap<Class<?>, MetaObject>();
    
    /** The set of converters (need a default set). */
    private final Converter converter;

    /**
     * Create string beans configuration that will treat the given set of
     * classes as beans to serialize and use the given converter to convert all
     * other classes to strings.
     * 
     * @param beans
     *            The set of classes that are beans.
     * @param converter
     *            The object to string converter.
     */
    public Stringer(ClassAssociation<Class<? extends MetaObject>> beans, Converter converter) {
        this.beans = new ClassAssociation<Class<? extends MetaObject>>(beans);
        this.converter = converter;
    }

    /**
     * Return true of the given class is considered a bean by this string bean
     * configuration. A bean will be inspected using reflection and all all
     * properties and fields will be serialized as name value pairs.
     * 
     * @param type
     *            The class type.
     * @return True if the given class is considered a bean by this string bean
     *         configuration.
     */
    public boolean isBean(Class<?> type) {
        if (type.isArray()) {
            throw new IllegalArgumentException();
        }
        if (type.isPrimitive()) {
            return false;
        }
        return BeanConstructor.class.isAssignableFrom(beans.get(type));
    }
    
    public MetaObject getMetaObject(Type objectType) {
        if (objectType instanceof ParameterizedType) {
            Class<?> objectClass = (Class<?>) ((ParameterizedType) objectType).getRawType();
            if (Map.class.isAssignableFrom(objectClass)) {
                
            } else if (Collection.class.isAssignableFrom(objectClass)) {
                return new MetaCollection((ParameterizedType) objectType);
            } else if (!Class.class.isAssignableFrom(objectClass)) {
                throw new IllegalStateException();
            }
            objectType = objectClass;
        } 
        if (objectType instanceof Class<?>) {
            final Class<?> objectClass = (Class<?>) objectType;
            MetaObject metaObject = metaObjectCache.get(objectClass);
            if (metaObject == null) {
                if (objectClass.isPrimitive()) {
                    metaObject = new MetaScalar(objectClass);
                } else {
                    Class<? extends MetaObject> metaObjectClass = beans.get(objectClass);
                    try {
                        try {
                            metaObject = metaObjectClass.getConstructor(Class.class).newInstance(objectClass);
                        } catch (Throwable e) {
                            throw new ReflectiveException(Reflective.encode(e), e);
                        }
                    } catch (ReflectiveException e) {
                        throw new StringBeanException(Stringer.class, "metaObjectCreate", e, metaObjectClass, objectClass);
                    }
                }
                metaObjectCache.put(objectClass, metaObject);
            }
            return metaObject;
        }
        throw new IllegalStateException();
    }

    /**
     * Get the converter used to convert objects into strings.
     * 
     * @return The converter.
     */
    public Converter getConverter() {
        return converter;
    }
}
