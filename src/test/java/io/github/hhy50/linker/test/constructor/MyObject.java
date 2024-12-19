package io.github.hhy50.linker.test.constructor;

public class MyObject {
    private final String name;

    public MyObject(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyObject) {
            return ((MyObject) obj).name.equals(this.name);
        }
        return false;
    }
}
