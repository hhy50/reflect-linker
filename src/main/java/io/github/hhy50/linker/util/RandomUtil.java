package io.github.hhy50.linker.util;

public class RandomUtil {

    static String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * STR.length());
            sb.append(STR.charAt(index));
        }
        return sb.toString();
    }
}
