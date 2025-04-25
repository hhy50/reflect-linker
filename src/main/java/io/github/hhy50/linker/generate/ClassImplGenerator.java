package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.InterfaceImplClass;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;

/**
 * The type Class impl generator.
 */
public class ClassImplGenerator {


    /**
     * Generate bytecode.
     *
     * @param defineClass the define class
     * @throws IOException the io exception
     */
    public static void generateBytecode(Class<?> define, Class<?> targetClass, InterfaceImplClass defineClass) throws IOException {
        String implClassName = defineClass.getClassName();
        InvokeClassImplBuilder classBuilder = InvokeClassImplBuilder
                .builder(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setTarget(targetClass)
                .setDefineClass(define);

        for (MethodDefine methodDefine : defineClass.getMethodDefines()) {
            Method method = methodDefine.method;
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getType(method), null)
                    .intercept(body -> generateMethodImpl(classBuilder, body, methodDefine));
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        String outputPath = System.getProperty("linker.output.path");
        if (!StringUtil.isEmpty(outputPath)) {
            Files.write(new File(outputPath, ClassUtil.toSimpleName(implClassName)+".class").toPath(), bytecode);
        }
        defineClass.setBytecode(bytecode);
    }

    private static void generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodBody body, MethodDefine methodDefine) {
        MethodHandle mh = null;
        if (methodDefine.hasGetter()) {
            mh = BytecodeFactory.generateGetter(classBuilder, methodDefine, methodDefine.fieldRef);
        } else if (methodDefine.hasSetter()) {
            mh = BytecodeFactory.generateSetter(classBuilder, methodDefine, methodDefine.fieldRef);
        } else if (methodDefine.hasConstructor()) {
            mh = BytecodeFactory.generateConstructor(classBuilder, methodDefine, (ConstructorRef) methodDefine.methodRef);
        } else if (methodDefine.methodRef != null) {
            mh = BytecodeFactory.generateInvoker(classBuilder, methodDefine, methodDefine.methodRef);
        } else {
            AsmUtil.throwNoSuchMethod(body.getWriter(), methodDefine.method.getName());
        }
        if (mh != null) {
            mh.define(classBuilder);
            mh.invoke(body);
        }
    }
}
