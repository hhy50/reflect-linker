package io.github.hhy50.linker.test.statictest;


/**
 * <p>StaticClass class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class StaticClass {

    private static String aaa = "1000";
    private String aaa2 = "1234";
    private static Object obj2 = new StaticClass2();
    /** Constant <code>obj3</code> */
    public static Object obj3 = new StaticClass2();

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
