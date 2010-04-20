package com.goodworkalan.stringbeans.jpa;

import org.testng.annotations.Test;

public class MetaJpaBeanTest {
    @Test
    public void introspection() {
        MetaJpaBean.introspect(String.class, Number.class);
    }
}
