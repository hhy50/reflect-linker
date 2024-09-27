package io.github.hhy50.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Target interface.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public interface Target {
    /**
     * The interface Bind.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.TYPE)
    public @interface Bind {
        /**
         * Value string.
         *
         * @return the string
         */
        String value();
    }
}
