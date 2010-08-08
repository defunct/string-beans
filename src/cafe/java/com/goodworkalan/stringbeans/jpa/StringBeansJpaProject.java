package com.goodworkalan.stringbeans.jpa;

import com.goodworkalan.cafe.ProjectModule;
import com.goodworkalan.cafe.builder.Builder;
import com.goodworkalan.cafe.outline.JavaProject;

/**
 * Builds the project definition for String Beans JPA.
 *
 * @author Alan Gutierrez
 */
public class StringBeansJpaProject implements ProjectModule {
    /**
     * Build the project definition for String Beans JPA.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.string-beans/string-beans-jpa/0.1.0.1")
                .depends()
                    .production("com.github.bigeasy.string-beans/string-beans/0.+1")
                    .production("org.hibernate/hibernate-core/3.3.1.GA")
                    .production("org.hibernate/hibernate-annotations/3.4.0.GA")
                    .development("org.testng/testng-jdk15/5.10")
                    .development("org.mockito/mockito-core/1.6")
                    .end()
                .end()
            .end();
    }
}
