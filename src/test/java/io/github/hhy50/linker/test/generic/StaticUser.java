package io.github.hhy50.linker.test.generic;

public class StaticUser {

    public static String getName(String name) {
        return name;
    }

    public static String getName() {
        return "default";
    }
}
