package com.goodworkalan.stringbeans.jpa.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class StringBeansJpaProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.string-beans/string-beans-jpa/0.1")
                .main()
                    .depends()
                        .include("com.goodworkalan/string-beans/0.1")
                        .include("org.hibernate/hibernate-core/3.3.1.GA")
                        .include("org.hibernate/hibernate-annotations/3.4.0.GA")
                        .end()
                    .end()
                .test()
                    .depends()
                        .include("org.testng/testng-jdk15/5.10")
                        .include("org.mockito/mockito-core/1.6")
                        .end()
                    .end()
                .end()
            .end();
    }
}
