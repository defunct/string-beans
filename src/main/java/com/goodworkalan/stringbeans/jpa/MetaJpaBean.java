package com.goodworkalan.stringbeans.jpa;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import com.goodworkalan.reflective.getter.Getter;
import com.goodworkalan.reflective.getter.Getters;
import com.goodworkalan.stash.Stash;
import com.goodworkalan.stringbeans.BeanConstructor;
import com.goodworkalan.stringbeans.MetaBean;
import com.goodworkalan.stringbeans.MetaObject;
import com.goodworkalan.stringbeans.StringBeanException;

/**
 * A meta bean that either creates a bean, or reads it from a JPA data source,
 * if the bean already exists in the JPA data source.
 * 
 * @author Alan Gutierrez
 */
public class MetaJpaBean implements BeanConstructor {
    /** The out-of-band data key for the entity manager. */
    public final static Stash.Key ENTITY_MANAGER = new Stash.Key();

    /** The delegate meta bean. */
    private final MetaObject metaBean;
    
    /** The name of id property. */
    private final String idPropertyName;

    /**
     * Create a meta JPA bean using the given entity object.
     * 
     * @param objectClass The JPA entity type.
     * @throws IllegalArgumentException
     *             If the given object is not annotated with {@link Entity} and
     *             therefore, not a JPA entity.
     */
    public MetaJpaBean(Class<?> objectClass) {
        if (objectClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException();
        }

        String idPropertyName = null;
        for (Getter getter : Getters.getGetters(objectClass).values()) {
            if (getter.getAccessibleObject().getAnnotation(Id.class) != null) {
                idPropertyName = getter.getName();
                break;
            }
        }

        if (idPropertyName == null) {
            throw new IllegalArgumentException();
        }

        this.metaBean = new MetaBean(objectClass);
        this.idPropertyName = idPropertyName;
    }
    
    // TODO Document.
    static BeanInfo introspect(Class<?> objectClass, Class<?> stopClass) {
        try {
            return Introspector.getBeanInfo(objectClass);
        } catch (IntrospectionException e) {
            throw new StringBeanException(MetaJpaBean.class, "introspection", objectClass);
        }
    }

    // TODO Document.
    public Class<?> getObjectClass() {
        return metaBean.getObjectClass();
    }
    
    // TODO Document.
    public Object newStackInstance() {
        return new HashMap<Object, Object>();
    }

    // TODO Document.
    public boolean isScalar() {
        return false;
    }
    
    // TODO Document.
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Map) object).put(name, value);
    }
    
    // TODO Document.
    public Type getPropertyType(String name) {
        return metaBean.getPropertyType(name);
    }
    
    // TODO Document.
    @SuppressWarnings("unchecked")
    public Object newInstance(Stash stash, Object object) {
        Object bean = null;
        Map<Object, Object> map = (Map) object;
        Object id = map.get(idPropertyName);
        if (id != null) {
            EntityManager em = stash.get(ENTITY_MANAGER, EntityManager.class);
            if (em == null) {
                throw new StringBeanException(MetaJpaBean.class, "entityManagerMissing");
            }
            bean = em.find(getObjectClass(), id);
        }
        if (bean == null) {
            bean = metaBean.newInstance(stash, metaBean.newStackInstance());
        }
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            metaBean.set(bean, entry.getKey().toString(), entry.getValue());
        }
        return bean;
    }
}
