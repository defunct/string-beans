package com.goodworkalan.stringbeans;

import com.goodworkalan.cassandra.CassandraException;
import com.goodworkalan.cassandra.Report;

public class StringBeanException extends CassandraException {
    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    public StringBeanException(int code) {
        super(code, new Report());
    }
    
    public StringBeanException(int code, Throwable cause) {
        super(code, new Report(), cause);
    }
}
