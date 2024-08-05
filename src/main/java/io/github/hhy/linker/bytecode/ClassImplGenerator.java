package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.*;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;


public class ClassImplGenerator {

    private static final BytecodeClassLoader classLoader = new BytecodeClassLoader();

    public static Class<?> generateImplClass(InvokeClassDefine defineClass) {
        Class<?> define = defineClass.define;
        String target = defineClass.targetClass;
        String implClassName = define.getName() + "$impl";
        InvokeClassImplBuilder classBuilder = AsmUtil
                .defineImplClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setTarget(target);

        for (MethodDefine methodDefine : defineClass.methodDefines) {
            Method method = methodDefine.define;
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null)
                    .accept(writer -> {
                        generateMethodImpl(classBuilder, writer, methodDefine);
                    });
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        return classLoader.load(implClassName, bytecode);
    }

    private static void generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodVisitor writer, MethodDefine methodDefine) {
        TargetPoint targetPoint = methodDefine.targetPoint;
        BytecodeGenerator codeGenerator = BytecodeFactory.getCodeGenerator(methodDefine.targetPointType, targetPoint);
        codeGenerator.generate(classBuilder, writer);
    }
}
