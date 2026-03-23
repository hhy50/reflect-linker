package io.github.hhy50.linker.define.method.buildin;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;

public class StringBuildinFunction implements BuildinFunction {


    @Override
    public boolean matches(String name, String[] parameterTypes) {
        return name.equals("__string__") && parameterTypes.length == 1;
    }

    @Override
    public MethodRef toMethodRef() {
        try {
            return new EarlyMethodRef(StringBuildinFunction.class.getDeclaredMethod("toString", Object.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Object o) {
        return o.toString();
    }
}
