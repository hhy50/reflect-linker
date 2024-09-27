package io.github.hhy50.linker.util;

/**
 * The type Util.
 */
public class Util {
    /**
     * Gets or else default.
     *
     * @param value       the value
     * @param defaultVale the default vale
     * @return the or else default
     */
    public static String getOrElseDefault(String value, String defaultVale) {
        if (value == null) return defaultVale;
        if (value.equals("")) return defaultVale;
        return value;
    }
}
