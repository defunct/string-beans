package com.goodworkalan.stringbeans;

import com.goodworkalan.danger.ContextualDanger;

// TODO Document.
public class StringBeanException extends ContextualDanger {
    /** The serial version id. */
    private static final long serialVersionUID = 1L;

    // TODO Document.
    public StringBeanException(Class<?> context, String code, Object...arguments) {
        super(context, code, null, arguments);
    }
    
    // TODO Document.
    public StringBeanException(Class<?> context, String code, Throwable cause, Object...arguments) {
        super(context, code, cause, arguments);
    }
}
