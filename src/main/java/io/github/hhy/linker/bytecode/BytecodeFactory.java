package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.RuntimeField;
import org.objectweb.asm.MethodVisitor;

public class BytecodeFactory {

    public static void generateSetter(InvokeClassImplBuilder classBuilder, MethodVisitor writer, RuntimeField targetPoint) {
        //new Getter(classBuilder, writer, targetPoint)
//        new Getter(classBuilder.bindTarget, );
    }
}
