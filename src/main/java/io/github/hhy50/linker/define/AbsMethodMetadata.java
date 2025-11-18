package io.github.hhy50.linker.define;

import java.lang.annotation.Annotation;

public class AbsMethodMetadata {

    /**
     *
     */
    private java.lang.reflect.Method reflect;

    /**
     *
     */
    private Annotation uniqueAnno;

    private Annotation[] annotations;

    private boolean isRuntime;

    private boolean isAutolink;
}
