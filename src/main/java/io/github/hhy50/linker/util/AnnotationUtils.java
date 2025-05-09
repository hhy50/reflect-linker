package io.github.hhy50.linker.util;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.annotations.Typed;

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
        return typedAnno != null ? typedAnno.value() : null;
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
     * Gets designate static tokens.
     *
     * @param method       the method
     * @param defaultToken the default token
     * @return the designate static tokens
     */
    public static Map<String, Boolean> getDesignateStaticTokens(Method method, String defaultToken) {
        Runtime.Static[] staticAnnos = method.getAnnotationsByType(Runtime.Static.class);
        if (staticAnnos == null) {
            return Collections.emptyMap();
        }

        Map<String, Boolean> s = new HashMap<>();
        for (Runtime.Static staticAnno : staticAnnos) {
            if (staticAnno.name().length == 0) {
                s.put(defaultToken, staticAnno.value());
            } else {
                for (String k : staticAnno.name()) {
                    s.put(k, staticAnno.value());
                }
            }
        }
        return s;
    }

    /**
     * Is autolink boolean.
     *
     * @param method the method
     * @return the boolean
     */
    public static boolean isAutolink(Method method) {
        return (method.getDeclaredAnnotation(Autolink.class) != null ||
                method.getDeclaringClass().getDeclaredAnnotation(Autolink.class) != null);
    }
}
