package io.github.hhy50.linker.define.method.buildin;

import io.github.hhy50.linker.define.method.MethodRef;

import java.util.Arrays;
import java.util.List;

public interface BuildinFunction {
    List<BuildinFunction> BUILDIN_FUNCTIONS = Arrays.asList(new CharBuildinFunction(), new ClassBuildinFunction(), new StringBuildinFunction());

    /**
     *
     * @param name
     * @param parameterTypes
     * @return
     */
    public boolean matches(String name, String[] parameterTypes);


    public MethodRef toMethodRef();

    static BuildinFunction matchesBuildinFunction(String name, String[] parameterTypes) {
        for (BuildinFunction buildinFunction : BUILDIN_FUNCTIONS) {
            if (buildinFunction.matches(name, parameterTypes)) return buildinFunction;
        }
        return null;
    }
}
