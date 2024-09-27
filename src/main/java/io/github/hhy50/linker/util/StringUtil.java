package io.github.hhy50.linker.util;

/**
 * The type String util.
 */
public class StringUtil {

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Is not empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
