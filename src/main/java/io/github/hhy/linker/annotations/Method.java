package io.github.hhy.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Method {

    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({ElementType.METHOD})
    public @interface Sign {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({ElementType.METHOD})
    @interface DynamicSign {
        String value();
    }
}
