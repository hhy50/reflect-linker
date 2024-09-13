package io.github.hhy50.linker.util;

/**
 * <p>Util class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class Util {
    /**
     * <p>getOrElseDefault.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @param defaultVale a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getOrElseDefault(String value, String defaultVale) {
        if (value == null) return defaultVale;
        if (value.equals("")) return defaultVale;
        return value;
    }
}
