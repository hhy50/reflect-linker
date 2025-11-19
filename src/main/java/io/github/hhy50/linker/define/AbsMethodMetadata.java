package io.github.hhy50.linker.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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

    public void setReflectMethod(Method reflect) {
        this.reflect = reflect;
    }
}
