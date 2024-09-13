package io.github.hhy50.linker.annotations;


import java.lang.annotation.Target;
import java.lang.annotation.*;

/**
 * <p>Typed class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Repeatable(Types.class)
public @interface Typed {
    String name() default "";
    String type();
}
