package io.github.hhy.linker.test.statictest;

/**
 * <p>StaticClass2 class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class StaticClass2 {

    private static String aaa = "1000";
    private String aaa2 = "1234";
    private static StaticClass3 obj3;

    /**
     * <p>getA.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getA() {
        return aaa;
    }

    /**
     * <p>get2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String get2() {
        return aaa;
    }
}
