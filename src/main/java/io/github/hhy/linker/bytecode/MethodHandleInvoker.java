package io.github.hhy.linker.bytecode;


import org.objectweb.asm.MethodVisitor;

public abstract class MethodHandleInvoker {

    protected String mhVar;

    public MethodHandleInvoker(String mhVar) {
        this.mhVar = mhVar;
    }

    public MethodHandleInvoker() {
    }

    /**
     * 定义 MethodHandle
     *
     * @param classImplBuilder
     */
    public abstract void define(InvokeClassImplBuilder classImplBuilder);

    /**
     * 生成调用
     */
    public void invoke(MethodVisitor write) {
        if (mhVar != null) {

        }
    }
}

