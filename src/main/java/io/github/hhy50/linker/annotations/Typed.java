package io.github.hhy50.linker.annotations;


import java.lang.annotation.*;
import java.lang.annotation.Target;

/**
 * <p>Typed class.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Repeatable(Types.class)
public @interface Typed {
    /**
     * Name string.
     *
     * @return the string
     */
    String name() default "";

    /**
     * Type string.
     *
     * @return the string
     */
    String value();
}
