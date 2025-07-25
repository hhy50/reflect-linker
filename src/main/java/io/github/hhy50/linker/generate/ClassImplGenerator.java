package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.define.InterfaceImplClass;
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
     * @param define      the define
     * @param targetClass the target class
     * @param defineClass the define class
     * @throws IOException the io exception
     */
    public static void generateBytecode(Class<?> define, Class<?> targetClass, InterfaceImplClass defineClass) throws IOException {
        String implClassName = defineClass.getClassName();
        InvokeClassImplBuilder classBuilder = InvokeClassImplBuilder
                .builder(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setTarget(targetClass)
                .setDefineClass(define);

        for (AbsMethodDefine absMethodDefine : defineClass.getAbsMethods()) {
            Method method = absMethodDefine.method;
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getType(method), null)
                    .intercept(body -> generateMethodImpl(classBuilder, body, absMethodDefine));
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        String outputPath = System.getProperty("linker.output.path");
        if (!StringUtil.isEmpty(outputPath)) {
            Files.write(new File(outputPath, ClassUtil.toSimpleName(implClassName)+".class").toPath(), bytecode);
        }
        defineClass.setBytecode(bytecode);
    }

    private static void generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodBody body, AbsMethodDefine absMethodDefine) {
        MethodHandle mh = null;
        if (absMethodDefine.hasGetter()) {
            mh = BytecodeFactory.generateGetter(classBuilder, absMethodDefine, absMethodDefine.fieldRef);
        } else if (absMethodDefine.hasSetter()) {
            mh = BytecodeFactory.generateSetter(classBuilder, absMethodDefine, absMethodDefine.fieldRef);
        } else if (absMethodDefine.methodRef != null) {
            mh = BytecodeFactory.generateInvoker(classBuilder, absMethodDefine, absMethodDefine.methodRef);
        } else {
            AsmUtil.throwNoSuchMethod(body.getWriter(), absMethodDefine.method.getName());
        }
        if (mh != null) {
            mh.define(classBuilder);
            mh.invoke(body);
        }
    }
}
