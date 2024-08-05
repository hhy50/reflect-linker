package io.github.hhy.linker.bytecode;

public abstract class MethodHandleInvoker {

    /**
     * 定义methodhandle-setter
     *
     * @param classImplBuilder
     */
    public abstract void define(InvokeClassImplBuilder classImplBuilder);
}

