package com.goodworkalan.stringbeans.jpa;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import com.goodworkalan.stash.Stash;
import com.goodworkalan.stringbeans.BeanConstructor;
import com.goodworkalan.stringbeans.MetaBean;
import com.goodworkalan.stringbeans.MetaObject;
import com.goodworkalan.stringbeans.ObjectBucket;
import com.goodworkalan.stringbeans.StringBeanException;

public class MetaJpaBean implements BeanConstructor {
    public final static Stash.Key ENTITY_MANAGER = new Stash.Key();

    private final MetaObject metaBean;
    
    private final String idPropertyName;
    
    public MetaJpaBean(Class<?> objectClass) {
        if (objectClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException();
        }

        String idPropertyName = null;
        for (Field field : objectClass.getFields()) {
            if (field.getAnnotation(Id.class) != null) {
                idPropertyName = field.getName();
                break;
            }
        }

        if (idPropertyName == null) {
            BeanInfo beanInfo = introspect(objectClass, Object.class);
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                Method method = property.getReadMethod();
                if (method != null && method.getAnnotation(Id.class) != null) {
                    idPropertyName = property.getName();
                    break;
                }
            }
        }

        if (idPropertyName == null) {
            throw new IllegalArgumentException();
        }

        this.metaBean = new MetaBean(objectClass);
        this.idPropertyName = idPropertyName;
    }
    
    static BeanInfo introspect(Class<?> objectClass, Class<?> stopClass) {
        try {
            return Introspector.getBeanInfo(objectClass);
        } catch (IntrospectionException e) {
            throw new StringBeanException(MetaJpaBean.class, "introspection", objectClass);
        }
    }

    public Class<?> getObjectClass() {
        return metaBean.getObjectClass();
    }
    
    public Object newStackInstance() {
        return new HashMap<Object, Object>();
    }

    public boolean isScalar() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public void set(Object object, String name, Object value) {
        ((Map) object).put(name, value);
    }
    
    public Type getPropertyType(String name) {
        return metaBean.getPropertyType(name);
    }
    
    public Iterable<ObjectBucket> buckets(Object object) {
        return metaBean.buckets(object);
    }
    
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