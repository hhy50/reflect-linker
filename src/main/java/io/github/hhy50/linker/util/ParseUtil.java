package io.github.hhy50.linker.util;

import java.lang.reflect.Parameter;

public class ParseUtil {
    public static String getRawType(Parameter item) {
        String typed = AnnotationUtils.getTyped(item);
        if (StringUtil.isNotEmpty(typed)) {
            return typed;
        }
        Class<?> type = item.getType();
        String bind = AnnotationUtils.getBind(type);
        if (StringUtil.isNotEmpty(bind)) {
            return bind;
        }
        return type.getCanonicalName();
    }
}
