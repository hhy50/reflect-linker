package io.github.hhy50.linker.util;

import java.lang.reflect.Parameter;

/**
 * The type Parse util.
 */
public class ParseUtil {
    /**
     * Gets raw type.
     *
     * @param item the item
     * @return the raw type
     */
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
