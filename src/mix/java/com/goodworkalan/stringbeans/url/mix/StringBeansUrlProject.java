package com.goodworkalan.stringbeans.url.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class StringBeansUrlProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.string-beans/string-beans-url/0.+1")
                .main()
                    .depends()
                        .include("com.github.bigeasy.string-beans/string-beans/0.+1")
                        .include("com.github.bigeasy.infuse/infuse/0.+1")
                        .include("com.github.bigeasy.danger/danger/0.+1")
                        .include("com.github.bigeasy.diffuse/diffuse/0.+1")
                        .include("com.github.bigeasy.reflective/reflective/0.+1")
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
