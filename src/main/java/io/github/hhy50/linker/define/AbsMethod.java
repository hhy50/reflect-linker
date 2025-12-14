package io.github.hhy50.linker.define;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;

import java.lang.reflect.Method;

public interface AbsMethod {

    /**
     *
     * @return
     */
    AbsMethodMetadata getMetadata();

    /**
     *
     * @return
     */
    Method getReflect();
}
