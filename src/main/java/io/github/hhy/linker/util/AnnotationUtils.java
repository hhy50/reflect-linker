package io.github.hhy.linker.util;

import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Static;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnnotationUtils {
    public static String getBind(Class<?> clazz) {
        Target.Bind bindAnno = clazz.getDeclaredAnnotation(Target.Bind.class);
        return bindAnno == null ? null : bindAnno.value();
    }

    public static String getTyped(Parameter parameter) {
        Typed typedAnno = parameter.getDeclaredAnnotation(Typed.class);
        return typedAnno != null ? typedAnno.name() : null;
    }

    public static boolean isRuntime(Method method) {
        return method.getDeclaredAnnotation(Runtime.class) != null;
    }

    public static boolean isRuntime(Class<?> bindTarget) {
        Target.Bind bindAnno = bindTarget.getDeclaredAnnotation(Target.Bind.class);
        return bindAnno.runtime();
    }

    public static Map<String, Boolean> getDesignateStaticFields(Method method) {
        Static[] staticAnnos =  method.getAnnotationsByType(Static.class);
        if (staticAnnos == null) {
            return Collections.emptyMap();
        }

        Map<String, Boolean> s = new HashMap<>();
        for (Static staticAnno : staticAnnos) {
            for (String k : staticAnno.name()) {
                s.put(k, staticAnno.value());
            }
        }
        return s;
    }
}
