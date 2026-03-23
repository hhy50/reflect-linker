package io.github.hhy50.linker.define.method.buildin;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;

public class CharBuildinFunction implements BuildinFunction {

    @Override
    public boolean matches(String name, String[] parameterTypes) {
        return name.equals("char") && parameterTypes.length == 1
                && parameterTypes[0].equals(String.class.getName());
    }

    @Override
    public MethodRef toMethodRef() {
        try {
            return new EarlyMethodRef(CharBuildinFunction.class.getDeclaredMethod("toChar", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static char toChar(String str) {
        if (str.length() == 1) {
            return str.charAt(0);
        }
        throw new ClassCastException("String['" + str + "'] is not a char");
    }
}
