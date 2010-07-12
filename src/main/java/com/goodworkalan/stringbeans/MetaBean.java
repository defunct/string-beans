package com.goodworkalan.stringbeans;

import java.lang.reflect.Type;
import java.util.Map;

import com.goodworkalan.danger.Danger;
import com.goodworkalan.reflective.setter.Setter;
import com.goodworkalan.reflective.setter.Setters;

/**
 * The basic serialization operations and properties for a Java Bean.
 * 
 * @author Alan Gutierrez
 */
public class MetaBean implements BeanConstructor {
    /** The bean class. */
    private final Class<?> objectClass;

    /**
     * Create a meta bean for the given bean class.
     * 
     * @param objectClass
     *            The bean class.
     */
    public MetaBean(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * Create a new instance of the bean to place onto the stack to collect
     * property values.
     * 
     * @return A new bean instance.
     */
    public Object newStackInstance() {
        try {
            return objectClass.newInstance();
        } catch (Exception e) {
            throw new Danger(e, MetaBean.class, "newInstance");
        }
    }

    /**
     * Get the bean class.
     * 
     * @return The bean class.
     */
    public Class<?> getObjectClass() {
        return objectClass;
    }

    /**
     * Get the type of the given property.
     * 
     * @return The property type.
     */
    public Type getPropertyType(String name) {
        Setter setter =  Setters.getGetters(objectClass).get(name);
        if (setter == null) {
            return null;
        }
        return setter.getGenericType();
    }

    /**
     * Set the property of the given object with the given name to the given
     * value.
     * 
     * @param object
     *            The object.
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    public void set(Object object, String name, Object value) {
        try {
            Setters.getGetters(objectClass).get(name).set(object, value);
        } catch (Exception e) {
            throw new Danger(e, MetaBean.class, "fieldSet", e);
        }
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
     * Returns the given <code>object</code> since we returned a bean of the
     * appropriate type from {@link #newStackInstance() newStackInstance}.
     * 
     * @param participants
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param object
     *            The object taken from the stack.
     * @return The given object.
     */
    public Object newInstance(Map<Object, Object> participants, Object object) {
        return object;
    }
}
