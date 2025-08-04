package io.github.hhy50.linker.test.primitive;

public class PrimitiveClassImpl implements PrimitiveClass {
    @Override
    public double doubleValue() {
        return 1.1;
    }

    @Override
    public float floatValue() {
        return 2.2f;
    }

    @Override
    public long longValue() {
        return 3;
    }

    @Override
    public int intValue() {
        return 4;
    }
}
