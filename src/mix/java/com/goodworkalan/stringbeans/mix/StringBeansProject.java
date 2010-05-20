package com.goodworkalan.stringbeans.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

/**
 * Builds the project definition for String Beans.
 *
 * @author Alan Gutierrez
 */
public class StringBeansProject implements ProjectModule {
    /**
     * Build the project definition for String Beans.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.string-beans/string-beans/0.1.0.2")
                .depends()
                    .production("com.github.bigeasy.stash/stash/0.+1")
                    .production("com.github.bigeasy.danger/danger/0.+1")
                    .production("com.github.bigeasy.class-association/class-association/0.+1")
                    .production("com.github.bigeasy.diffuse/diffuse/0.+1")
                    .production("com.github.bigeasy.reflective/reflective/0.+1")
                    .development("org.testng/testng-jdk15/5.10")
                    .development("org.mockito/mockito-core/1.6")
                    .end()
                .end()
            .end();
    }
}
