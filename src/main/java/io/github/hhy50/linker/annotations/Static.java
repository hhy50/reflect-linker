package io.github.hhy50.linker.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于给runtime字段强制指定是否是静态字段/方法
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface Static {
    /**
     * Value boolean.
     *
     * @return the boolean
     */
    boolean value() default true;

    /**
     * Name string [ ].
     *
     * @return the string [ ]
     */
    String[] name() default {};
}
