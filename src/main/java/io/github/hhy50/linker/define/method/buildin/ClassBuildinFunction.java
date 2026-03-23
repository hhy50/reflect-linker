package io.github.hhy50.linker.define.method.buildin;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;

public class ClassBuildinFunction implements BuildinFunction {

    @Override
    public boolean matches(String name, String[] parameterTypes) {
        return name.equals("class") && parameterTypes.length == 1
                && parameterTypes[0].equals(String.class.getName());
    }

    @Override
    public MethodRef toMethodRef() {
        try {
            return new EarlyMethodRef(Class.class.getDeclaredMethod("forName", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
