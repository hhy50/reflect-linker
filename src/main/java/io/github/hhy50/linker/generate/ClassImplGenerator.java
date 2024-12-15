package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.InterfaceImplClassDefine;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;


/**
 * The type Class impl generator.
 */
public class ClassImplGenerator {


    /**
     * Generate bytecode.
     *
     * @param defineClass the define class
     * @param cl          the cl
     * @throws IOException the io exception
     */
    public static void generateBytecode(InterfaceImplClassDefine defineClass, final ClassLoader cl) throws IOException {
        if (cl == null) {
            throw new IllegalArgumentException("classLoader must not be null");
        }

        Class<?> define = defineClass.getDefine();
        String implClassName = defineClass.getClassName();
        Class<?> targetClass = defineClass.getTargetClass();
        InvokeClassImplBuilder classBuilder = InvokeClassImplBuilder
                .builder(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setTarget(targetClass)
                .setDefineClass(define);

        List<MethodDefine> methodDefines = defineClass.getMethodDefines();
        methodDefines.sort(Comparator.comparing(MethodDefine::getName));

        for (MethodDefine methodDefine : methodDefines) {
            Method method = methodDefine.define;
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null)
                    .accept(body -> {
                        generateMethodImpl(classBuilder, body, methodDefine);
                    });
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
        } else if (methodDefine.methodRef != null) {
            mh = BytecodeFactory.generateInvoker(classBuilder, methodDefine, methodDefine.methodRef);
        } else {
            AsmUtil.throwNoSuchMethod(body.getWriter(), methodDefine.define.getName());
        }
        if (mh != null) {
            mh.define(classBuilder);
            mh.invoke(body);
        }
    }
}
