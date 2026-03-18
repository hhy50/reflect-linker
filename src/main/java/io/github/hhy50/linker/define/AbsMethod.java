package io.github.hhy50.linker.define;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;

import java.lang.reflect.Method;

/**
 * The interface Abs method.
 */
public interface AbsMethod {

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    AbsMethodMetadata getMetadata();

    /**
     * Gets reflect.
     *
     * @return the reflect
     */
    Method getReflect();
}
