package io.github.hhy50.linker.test.v1.nest.case5;

public class Out {
    Inner inner;

    @Override
    public String toString() {
        return "i am out";
    }

    public static class Inner {
        @Override
        public String toString() {
            return "i am inner";
        }
    }
}