package com.goodworkalan.stringbeans.jpa;

import org.testng.annotations.Test;

// TODO Document.
public class MetaJpaBeanTest {
    // TODO Document.
    @Test
    public void introspection() {
        MetaJpaBean.introspect(String.class, Number.class);
    }
}
