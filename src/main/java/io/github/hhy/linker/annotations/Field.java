package io.github.hhy.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Field {

    /**
     * 获取指定字段值
     * 这个字段可以是private | static
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Getter {
        String value() default "";
    }

    /**
     * 设置指定字段值
     * 这个字段可以是private | static | final
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Setter {
        String value() default "";
    }
}
