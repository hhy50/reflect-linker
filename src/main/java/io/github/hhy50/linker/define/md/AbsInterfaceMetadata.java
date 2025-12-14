package io.github.hhy50.linker.define.md;

import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.generate.builtin.SetTargetProvider;
import io.github.hhy50.linker.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class AbsInterfaceMetadata {

    /**
     *
     */
    private final java.lang.Class<?> interfaceClass;

    private final List<Annotation> annotations = new ArrayList<>();

    private final Map<String, String> types = new HashMap<>();

    private final boolean runtime;

    private final Class<?> targetClass;

    public AbsInterfaceMetadata(Class<?> define, Class<?> targetClass) {
        this.interfaceClass = define;
        this.runtime = AnnotationUtils.isRuntime(define)
                || SetTargetProvider.class.isAssignableFrom(define)
                || define == Object.class;
        this.targetClass = targetClass;
        for (Annotation anno : define.getDeclaredAnnotations()) {
            addAnnotation(anno);
        }
    }

    public void addAnnotation(Annotation annotation) {
        if (annotation instanceof Typed) {
            Typed typed = (Typed) annotation;
            String type = this.types.put(typed.name(), typed.value());
            if (type != null && !type.equals(typed.value())) {
                throw new VerifyException(
                        "@Typed of field '" + typed.name() + "' defined twice is inconsistent");
            }
            this.annotations.add(annotation);
        }
    }

    public boolean isRuntime() {
        return this.runtime;
    }

    public List<Method> getMethods() {
        return Arrays.asList(interfaceClass.getDeclaredMethods());
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
