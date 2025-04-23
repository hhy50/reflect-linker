package io.github.hhy50.linker.util;

import io.github.hhy50.linker.annotations.*;
import io.github.hhy50.linker.annotations.Runtime;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Annotation utils.
 */
public class AnnotationUtils {
    /**
     * Gets bind.
     *
     * @param clazz the clazz
     * @return the bind
     */
    public static String getBind(Class<?> clazz) {
        Target.Bind bindAnno = clazz.getDeclaredAnnotation(Target.Bind.class);
        return bindAnno == null ? null : bindAnno.value();
    }

    /**
     * Gets typed.
     *
     * @param parameter the parameter
     * @return the typed
     */
    public static String getTyped(Parameter parameter) {
        Typed typedAnno = parameter.getDeclaredAnnotation(Typed.class);
        return typedAnno != null ? typedAnno.type() : null;
    }

    /**
     * Is runtime boolean.
     *
     * @param method the method
     * @return the boolean
     */
    public static boolean isRuntime(Method method) {
        return method.getDeclaredAnnotation(Runtime.class) != null;
    }

    /**
     * Is runtime boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    public static boolean isRuntime(Class<?> clazz) {
        return clazz.getDeclaredAnnotation(Runtime.class) != null;
    }

    /**
     * Gets designate static fields.
     *
     * @param method    the method
     * @param lastToken the last token
     * @return the designate static fields
     */
    public static Map<String, Boolean> getDesignateStaticFields(Method method, String lastToken) {
        Static[] staticAnnos = method.getAnnotationsByType(Static.class);
        if (staticAnnos == null) {
            return Collections.emptyMap();
        }

        Map<String, Boolean> s = new HashMap<>();
        for (Static staticAnno : staticAnnos) {
            if (staticAnno.name().length == 0) {
                s.put(lastToken, staticAnno.value());
            } else {
                for (String k : staticAnno.name()) {
                    s.put(k, staticAnno.value());
                }
            }
        }
        return s;
    }

    /**
     * Is auto link result boolean.
     *
     * @param method the method
     * @return the boolean
     */
    public static boolean isAutolink(Method method) {
        return method.getDeclaredAnnotation(Autolink.class) != null ||
                method.getDeclaringClass().getDeclaredAnnotation(Autolink.class) != null;
    }
}
