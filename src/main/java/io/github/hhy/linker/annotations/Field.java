package io.github.hhy.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Field {

    /**
     *  <p>获取指定字段值</p>
     * 这个字段可以是 private | static, 支持获取链式字段 a.b.c
     * 这个注解方法的签名返回值必须是void, 并且参数只能有一个
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Getter {
        String value() default "";
    }

    /**
     * <p>设置指定字段值</p>
     * 这个字段可以是private | static | final
     * 这个注解方法的签名返回值类型不能为void, 并且参数的长度必须为0
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Setter {
        String value() default "";
    }
}
