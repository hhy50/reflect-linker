package io.github.hhy.linker.util;

import io.github.hhy.linker.annotations.Target;

public class AnnotationUtils {
    public static String getBind(Class<?> clazz) {
        Target.Bind bindAnno = clazz.getDeclaredAnnotation(Target.Bind.class);
        return bindAnno == null ? null : bindAnno.value();
    }
}
