package com.goodworkalan.stringbeans;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.goodworkalan.utility.ClassAssociation;

/**
 * Build a {@link Stringer}, a String Beans configuration, for use with String
 * Beans parsers and emitters.
 * 
 * @author Alan Gutierrez
 */
public class StringerBuilder {
    /** The set of beans that can be read or written. */
    private final ClassAssociation<Class<? extends MetaObject>> beans = new ClassAssociation<Class<? extends MetaObject>>();

    private final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public StringerBuilder() {
    }

    /**
     * Treat the given <code>type</code> as a Java Bean, writing out fields and
     * bean properties as named values.
     * 
     * @param type
     *            The bean type.
     * @return This builder to continue configuration.
     */
    public StringerBuilder isBean(Class<?> type) {
        beans.exact(type, MetaBean.class);
        return this;
    }

    public StringerBuilder isBean(Class<?> type, Class<? extends BeanConstructor> meta) {
        beans.exact(type, meta);
        return this;
    }

    public StringerBuilder isBeanIfAssignableTo(Class<?> type) {
        beans.assignable(type, MetaBean.class);
        return this;
    }


    public StringerBuilder isBeanIfAssignableTo(Class<?> type, Class<? extends BeanConstructor> meta) {
        beans.assignable(type, meta);
        return this;
    }


    public StringerBuilder isBeanIfAnnotatedWith(Class<? extends Annotation> annotation) {
        beans.annotated(annotation, MetaBean.class);
        return this;
    }

    public StringerBuilder isBeanIfAnnotatedWith(Class<? extends Annotation> annotation, Class<? extends BeanConstructor> meta) {
        beans.annotated(annotation, meta);
        return this;
    }

    public StringerBuilder converter(Class<?> type, Converter converter) {
        converters.put(type, converter);
        return this;
    }

    public Stringer getInstance() {
        beans.assignable(Object.class, MetaScalar.class);
        return new Stringer(beans, new ConverterMap(converters));
    }
}
