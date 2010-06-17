package com.goodworkalan.stringbeans;

import com.goodworkalan.danger.ContextualDanger;

public class StringBeanException extends ContextualDanger {
    /** The serial version id. */
    private static final long serialVersionUID = 1L;

    public StringBeanException(Class<?> context, String code, Object...arguments) {
        super(context, code, null, arguments);
    }
    
    public StringBeanException(Class<?> context, String code, Throwable cause, Object...arguments) {
        super(context, code, cause, arguments);
    }
}
