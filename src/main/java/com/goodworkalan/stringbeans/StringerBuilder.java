package com.goodworkalan.stringbeans;

import java.lang.annotation.Annotation;

import com.goodworkalan.diffuse.Diffuser;
import com.goodworkalan.infuse.Infuser;
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

    /** The set of object to diffused object conversion strategies. */
    public final Diffuser diffuser = new Diffuser();

    /** The set of string to object conversion strategies. */
    public final Infuser infuser = new Infuser();

    /**
     * Create a new String Beans configuration builder.
     */
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

    public Stringer getInstance() {
        beans.assignable(Object.class, MetaScalar.class);
        return new Stringer(beans, new Diffuser(diffuser), new Infuser(infuser));
    }
}
