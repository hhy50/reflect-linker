package io.github.hhy50.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The interface Generate.
 */
public interface Generate {

    /**
     * The interface Builtin.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.TYPE)
    @interface Builtin {
        /**
         * Value class [ ].
         *
         * @return the class [ ]
         */
        Class<?>[] value() default {};
    }
}
