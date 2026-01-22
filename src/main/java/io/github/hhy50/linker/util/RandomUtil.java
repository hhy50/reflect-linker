package io.github.hhy50.linker.util;

public class RandomUtil {

    static String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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
