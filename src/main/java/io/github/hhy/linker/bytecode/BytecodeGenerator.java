package io.github.hhy.linker.bytecode;

import org.objectweb.asm.MethodVisitor;

public interface BytecodeGenerator {

    /**
     * 生成方法调用的字节码
     * @param writer
     */
    public void generate(InvokeClassImplBuilder classBuilder, MethodVisitor writer);
}
