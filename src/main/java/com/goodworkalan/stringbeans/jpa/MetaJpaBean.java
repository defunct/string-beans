package com.goodworkalan.stringbeans.jpa;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import com.goodworkalan.reflective.getter.Getter;
import com.goodworkalan.reflective.getter.Getters;
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
    public final static Object ENTITY_MANAGER = new Object();

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
    
    /**
     * Get the entity bean class.
     * 
     * @return The entity bean class.
     */
    public Class<?> getObjectClass() {
        return metaBean.getObjectClass();
    }

    /**
     * Create a new place holder map to place onto the object stack to gather
     * property valeus.
     * 
     * @return A place holder map.
     */
    public Object newStackInstance() {
        return new HashMap<Object, Object>();
    }

    /**
     * Return false indicating that the bean is not a scalar.
     * 
     * @return False indicating that the bean is not a scalar.
     */
    public boolean isScalar() {
        return false;
    }

    /**
     * Put a property into the place holder property map.
     * 
     * @param object
     *            The place holder property map.
     * @param name
     *            The property name.
     * @param value
     *            The property value.
     */
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Map) object).put(name, value);
    }
    
    /**
     * Get the type of the given property.
     * 
     * @return The property type.
     */
    public Type getPropertyType(String name) {
        return metaBean.getPropertyType(name);
    }

    /**
     * Create a new instance of the entity bean by first attempting to find it
     * in a JPA data source provided using a <code>EntityManager</code> provided
     * by the participants map, then constructing it if it cannot be found.
     * 
     * @param participants
     *            The heterogeneous container of unforeseen participants in the
     *            construction of beans in the object graph.
     * @param object
     *            The place holder object.
     * @return The stored entity bean or a new instance.
     */
    public Object newInstance(Map<Object, Object> participants, Object object) {
        Object bean = null;
        Map<?, ?> map = (Map<?, ?>) object;
        Object id = map.get(idPropertyName);
        if (id != null) {
            Object em = participants.get(ENTITY_MANAGER);
            if (em == null) {
                throw new StringBeanException(MetaJpaBean.class, "entityManagerMissing");
            }
            bean = ((EntityManager) em).find(getObjectClass(), id);
        }
        if (bean == null) {
            bean = metaBean.newInstance(participants, metaBean.newStackInstance());
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            metaBean.set(bean, entry.getKey().toString(), entry.getValue());
        }
        return bean;
    }
}
