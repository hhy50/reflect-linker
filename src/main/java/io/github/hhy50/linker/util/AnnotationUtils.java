package io.github.hhy50.linker.util;

import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Static;
import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.annotations.Typed;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>AnnotationUtils class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class AnnotationUtils {
    /**
     * <p>getBind.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getBind(Class<?> clazz) {
        Target.Bind bindAnno = clazz.getDeclaredAnnotation(Target.Bind.class);
        return bindAnno == null ? null : bindAnno.value();
    }

    /**
     * <p>getTyped.</p>
     *
     * @param parameter a {@link java.lang.reflect.Parameter} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getTyped(Parameter parameter) {
        Typed typedAnno = parameter.getDeclaredAnnotation(Typed.class);
        return typedAnno != null ? typedAnno.type() : null;
    }

    /**
     * <p>isRuntime.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     * @return a boolean.
     */
    public static boolean isRuntime(Method method) {
        return method.getDeclaredAnnotation(Runtime.class) != null;
    }

    /**
     * <p>isRuntime.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean isRuntime(Class<?> clazz) {
        return clazz.getDeclaredAnnotation(Runtime.class) != null;
    }

    /**
     * <p>getDesignateStaticFields.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     * @return a {@link java.util.Map} object.
     */
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
