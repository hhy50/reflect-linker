package io.github.hhy50.linker.util;

/**
 * The type Random util.
 */
public class RandomUtil {

    /**
     * The Str.
     */
    static String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Gets random string.
     *
     * @param length the length
     * @return the random string
     */
    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        int first = (int) (Math.random() * 26 * 2);
        sb.append(STR.charAt(first));

        for (int i = 1; i < length; i++) {
            int index = (int) (Math.random() * STR.length());
            sb.append(STR.charAt(index));
        }
        return sb.toString();
    }
}
