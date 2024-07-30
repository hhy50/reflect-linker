package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.asm.AsmUtil;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

public class InvokeBytecodeGenerator implements BytecodeGenerator {

    private final Method method;

    public InvokeBytecodeGenerator(Class<?> targetClass, Method method) {
        this.method = method;
    }

    @Override
    public void generate(AsmClassBuilder classBuilder, MethodVisitor writer) {
        AsmUtil.throwNoSuchMethod(writer, "aa");
    }
}
