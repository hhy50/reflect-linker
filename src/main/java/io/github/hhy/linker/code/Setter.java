package io.github.hhy.linker.code;

import io.github.hhy.linker.code.vars.ObjectVar;

public class Setter extends MethodHandle {

    public Setter(MethodHandle prevHandle) {
        super(prevHandle);
    }

    @Override
    public ObjectVar invoke() {
        return null;
    }
}
