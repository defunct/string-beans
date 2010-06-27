package com.goodworkalan.stringbeans;

import com.goodworkalan.danger.ContextualDanger;

/**
 * A general purpose exception for exceptional conditions within the String
 * Beans library.
 * 
 * @author Alan Gutierrez
 */
public class StringBeanException extends ContextualDanger {
    /** The serial version id. */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new String Beans exception with a message and no cause.
     * 
     * @param context
     *            The message bundle context.
     * @param code
     *            The message code.
     * @param arguments
     *            The message format arguments.
     */
    public StringBeanException(Class<?> context, String code, Object...arguments) {
        super(context, code, null, arguments);
    }

    /**
     * Create a new String Beans exception with a message and a cause.
     * 
     * @param context
     *            The message bundle context.
     * @param code
     *            The message code.
     * @param cause
     *            The cause.
     * @param arguments
     *            The message format arguments.
     */
    public StringBeanException(Class<?> context, String code, Throwable cause, Object...arguments) {
        super(context, code, cause, arguments);
    }
}
