package io.github.hhy.linker.util;

import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;

import java.lang.reflect.Parameter;

public class AnnotationUtils {
    public static String getBind(Class<?> clazz) {
        Target.Bind bindAnno = clazz.getDeclaredAnnotation(Target.Bind.class);
        return bindAnno == null ? null : bindAnno.value();
    }

    public static String getTyped(Object obj) {
        if (obj instanceof Parameter) {
            Parameter parameter = (Parameter) obj;
            Typed typedAnno = parameter.getDeclaredAnnotation(Typed.class);
            return typedAnno != null ? typedAnno.name() : null;
        }
        return null;
    }
}
