package io.github.hhy.linker.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class AnnotationUtil {


    public static String getOrElseDefault(Annotation anno, String defaultVale) {
        if (anno == null) return defaultVale;
        InvocationHandler annoInvocationHandle = Proxy.getInvocationHandler(anno);
        return defaultVale;
    }
}
