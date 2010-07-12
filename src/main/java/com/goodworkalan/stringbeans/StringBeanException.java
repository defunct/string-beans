package com.goodworkalan.stringbeans;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A general purpose exception for exceptional conditions within the String
 * Beans library.
 * 
 * @author Alan Gutierrez
 */
public class StringBeanException extends RuntimeException {
    /** The serial version id. */
    private static final long serialVersionUID = 1L;
    
    /** The message context class. */
    public final Class<?> contextClass;
    
    /** The message error code. */
    public final String code;

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
        this(null, context, code, arguments);
    }

    /**
     * Create a new String Beans exception with a message and a cause.
     * 
     * @param contextClass
     *            The message bundle context.
     * @param code
     *            The message code.
     * @param cause
     *            The cause.
     * @param arguments
     *            The message format arguments.
     */
    public StringBeanException( Throwable cause, Class<?> contextClass, String code,Object...arguments) {
        super(_(contextClass, code), cause);
        this.contextClass = contextClass;
        this.code = code;
    }

    /**
     * Format the exception message using the message arguments to format the
     * message found with the message key in the message bundle found in the
     * package of the given context class.
     * 
     * @param contextClass
     *            The context class.
     * @param code
     *            The error code.
     * @param arguments
     *            The format message arguments.
     * @return The formatted message.
     */
    public final static String _(Class<?> contextClass, String code, Object...arguments) {
        String baseName = contextClass.getPackage().getName() + ".exceptions";
        String messageKey = contextClass.getSimpleName() + "/" + code;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
            return String.format((String) bundle.getObject(messageKey), arguments);
        } catch (Exception e) {
            return String.format("Cannot load message key [%s] from bundle [%s] becuase [%s].", messageKey, baseName, e.getMessage());
        }
    }
    
    /**
     * Assert that the given exception is a reflection related
     * <code>Exception</code>. .
     * 
     * @param e
     *            The throwable.
     * @return The throwable.
     * @exception RuntimeException
     *                if the throwable is an <code>RuntimeException</code> but
     *                not an <code>IllegalArgumentException</code>.
     * @exception Error
     *                if the throwable is an <code>Error</code> but not an
     *                <code>ExceptionInInitializerError</code>.
     */
    public static Throwable $(Throwable e) {
        if (e instanceof Error) { 
            if (!(e instanceof ExceptionInInitializerError)) {
                throw (Error) e;
            }
        } else if (e instanceof RuntimeException) {
            if (!(e instanceof SecurityException) && !(e instanceof IllegalArgumentException)) {
                throw (RuntimeException) e;
            }
        }
        return e;
    }
}
