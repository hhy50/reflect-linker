package io.github.hhy50.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Field interface.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public interface Field {

    /**
     * <p>获取指定字段值</p>
     * 这个字段可以是 private | static, 支持获取链式字段 a.b.c
     * 这个注解方法的返回值不能是void, 并且参数的长度必须为0
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Getter {
        /**
         * Value string.
         *
         * @return the string
         */
        String value() default "";
    }

    /**
     * <p>设置指定字段值</p>
     * 这个字段可以是private | static, 但不能是 final
     * 这个注解方法的返回值类型必须为void, 并且参数的长度必须为1
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Setter {
        /**
         * Value string.
         *
         * @return the string
         */
        String value() default "";
    }
}
