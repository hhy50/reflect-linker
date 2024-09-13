package io.github.hhy50.linker.util;

/**
 * <p>StringUtil class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class StringUtil {

    /**
     * <p>isEmpty.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>isNotEmpty.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
