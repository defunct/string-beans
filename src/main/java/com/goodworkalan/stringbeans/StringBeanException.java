package com.goodworkalan.stringbeans;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.goodworkalan.danger.ContextualDanger;

public class StringBeanException extends ContextualDanger {
    /** The serial version id. */
    private static final long serialVersionUID = 1L;

    /** A cache of resource bundles. */
    private final static ConcurrentHashMap<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

    public StringBeanException(Class<?> context, String code, Object...arguments) {
        super(bundles, context, code, null, arguments);
    }
    
    public StringBeanException(Class<?> context, String code, Throwable cause, Object...arguments) {
        super(bundles, context, code, cause, arguments);
    }
}
