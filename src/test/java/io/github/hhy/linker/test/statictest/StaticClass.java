package io.github.hhy.linker.test.statictest;


public class StaticClass {

    private static String aaa = "1000";
    private String aaa2 = "1234";
    private static Object obj2 = new StaticClass2();
    public static Object obj3 = new StaticClass2();

    public static String getA() {
        return aaa;
    }

    public String get2() {
        return aaa;
    }
}
