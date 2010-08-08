package com.goodworkalan.stringbeans;

import com.goodworkalan.cafe.ProjectModule;
import com.goodworkalan.cafe.builder.Builder;
import com.goodworkalan.cafe.outline.JavaProject;

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
                .produces("com.github.bigeasy.string-beans/string-beans/0.1.0.5")
                .depends()
                    .production("com.github.bigeasy.class/class-boxer/0.+1")
                    .production("com.github.bigeasy.infuse/infuse/0.+1")
                    .production("com.github.bigeasy.danger/danger/0.+3")
                    .production("com.github.bigeasy.class/class-association/0.+1")
                    .production("com.github.bigeasy.diffuse/diffuse/0.+1")
                    .production("com.github.bigeasy.reflective/reflective-setter/0.+1")
                    .development("org.testng/testng-jdk15/5.10")
                    .development("org.mockito/mockito-core/1.6")
                    .end()
                .end()
            .end();
    }
}
