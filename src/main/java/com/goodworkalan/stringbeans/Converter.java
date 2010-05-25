package com.goodworkalan.stringbeans;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.goodworkalan.diffuse.Diffuser;
import com.goodworkalan.infuse.Infuser;
import com.goodworkalan.reflective.Reflective;
import com.goodworkalan.reflective.ReflectiveException;
import com.goodworkalan.utility.ClassAssociation;

/**
 * String beans emitter and parser configuration.
 *
 * @author Alan Gutierrez
 */
public class Converter {
    /** The set of beans that can be read or written. */
    private final ClassAssociation<Class<? extends MetaObject>> beans;
    
    private final ConcurrentMap<Class<?>, MetaObject> metaObjectCache = new ConcurrentHashMap<Class<?>, MetaObject>();
    
    /** The set of converters (need a default set). */
    public final Infuser infuser;
    
    public final Diffuser diffuser;

    public Converter(Converter converter)  {
        this.beans = new ClassAssociation<Class<? extends MetaObject>>(converter.beans);
        this.diffuser = new Diffuser(converter.diffuser);
        this.infuser = new Infuser(converter.infuser);
    }
        
    public Converter() {
        this.beans = new ClassAssociation<Class<? extends MetaObject>>();
        this.diffuser = new Diffuser();
        this.infuser = new Infuser();
    }

    /**
     * Convert the given <code>object</code> to a string using the infuser.
     * 
     * @param type
     *            The type to create.
     * @param string
     *            The string to convert.
     * @return An object of the given type.
     */
    public Object fromString(Class<?> type, String string) {
        if (string == null) {
            return null;
        }
        return infuser.getInfuser(type).infuse(string);
    }

    /**
     * Convert the given <code>object</code> to a string.
     * 
     * @param object
     *            The object to convert to a string.
     * @return The converted string.
     */
    public String toString(Object object) {
        if (object == null) {
            return null;
        }
        return diffuser.diffuse(object).toString();
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
                        throw new StringBeanException(Converter.class, "metaObjectCreate", e, metaObjectClass, objectClass);
                    }
                }
                metaObjectCache.put(objectClass, metaObject);
            }
            return metaObject;
        }
        throw new IllegalStateException();
    }
    
    /**
     * Treat the given <code>type</code> as a Java Bean, writing out fields and
     * bean properties as named values.
     * 
     * @param type
     *            The bean type.
     * @return This builder to continue configuration.
     */
    public void setBean(Class<?> type) {
        beans.exact(type, MetaBean.class);
    }

    public void setBean(Class<?> type, Class<? extends BeanConstructor> meta) {
        beans.exact(type, meta);
    }

    public void setBeanIfAssignableTo(Class<?> type) {
        beans.assignable(type, MetaBean.class);
    }


    public void setBeanIfAssignableTo(Class<?> type, Class<? extends BeanConstructor> meta) {
        beans.assignable(type, meta);
    }


    public void setBeanIfAnnotatedWith(Class<? extends Annotation> annotation) {
        beans.annotated(annotation, MetaBean.class);
    }

    public void setBeanIfAnnotatedWith(Class<? extends Annotation> annotation, Class<? extends BeanConstructor> meta) {
        beans.annotated(annotation, meta);
    }
}
