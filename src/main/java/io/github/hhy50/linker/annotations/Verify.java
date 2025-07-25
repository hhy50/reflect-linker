package io.github.hhy50.linker.annotations;

import io.github.hhy50.linker.util.Verifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Verify.
 */
public interface Verify {

    /**
     * The interface Unique.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Unique {

    }

    /**
     * The interface Custom.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Custom {
        /**
         * Value class.
         *
         * @return the class
         */
        Class<? extends Verifier> value();
    }
}
