package io.github.hhy50.linker.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Types class.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface Types {
    /**
     * Value typed [ ].
     *
     * @return the typed [ ]
     */
    Typed[] value();
}
