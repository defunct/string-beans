package com.goodworkalan.stringbeans.json;

import com.goodworkalan.cafe.ProjectModule;
import com.goodworkalan.cafe.builder.Builder;
import com.goodworkalan.cafe.outline.JavaProject;

/**
 * Builds the project definition for String Beans JSON.
 *
 * @author Alan Gutierrez
 */
public class StringBeansJsonProject implements ProjectModule {
    /**
     * Build the project definition for String Beans JSON.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.string-beans/string-beans-json/0.1.0.2")
                .depends()
                    .production("com.github.bigeasy.string-beans/string-beans/0.+1.0.2")
                    .development("org.testng/testng-jdk15/5.10")
                    .development("org.mockito/mockito-core/1.6")
                    .end()
                .end()
            .end();
    }
}
