package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.InterfaceClassDefine;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;


/**
 * <p>ClassImplGenerator class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ClassImplGenerator {


    /**
     * <p>generateImplClass.</p>
     *
     * @param defineClass a {@link io.github.hhy50.linker.define.InterfaceClassDefine} object.
     * @param cl          a {@link java.lang.ClassLoader} object.
     * @throws java.io.IOException if any.
     */
    public static void generateBytecode(InterfaceClassDefine defineClass, final ClassLoader cl) throws IOException {
        if (cl == null) {
            throw new IllegalArgumentException("classLoader must not be null");
        }

        Class<?> define = defineClass.getDefine();
        String implClassName = define.getName()+"$impl";
        Class<?> targetClass = defineClass.getTargetClass();
        InvokeClassImplBuilder classBuilder = AsmUtil
                .defineImplClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setDefine(define)
                .setTarget(targetClass)
                .init();

        List<MethodDefine> methodDefines = defineClass.getMethodDefines();
        methodDefines.sort(Comparator.comparing(MethodDefine::getName));

        for (MethodDefine methodDefine : methodDefines) {
            Method method = methodDefine.define;
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null)
                    .accept(mv -> {
                        generateMethodImpl(classBuilder, mv, methodDefine);
                    });
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        String outputPath = System.getProperty("linker.output.path");
        if (!StringUtil.isEmpty(outputPath)) {
            Files.write(new File(outputPath+ClassUtil.toSimpleName(implClassName)+".class").toPath(), bytecode);
        }
        defineClass.setBytecode(bytecode);
    }

    private static void generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodVisitor mv, MethodDefine methodDefine) {
        MethodHandle mh = null;
        if (methodDefine.hasGetter()) {
            mh = BytecodeFactory.generateGetter(classBuilder, methodDefine, methodDefine.fieldRef);
        } else if (methodDefine.hasSetter()) {
            mh = BytecodeFactory.generateSetter(classBuilder, methodDefine, methodDefine.fieldRef);
        } else if (methodDefine.methodRef != null) {
            mh = BytecodeFactory.generateInvoker(classBuilder, methodDefine, methodDefine.methodRef);
        } else {
            AsmUtil.throwNoSuchMethod(mv, methodDefine.define.getName());
        }
        if (mh != null) {
            mh.define(classBuilder);
            mh.invoke(new MethodBody(classBuilder, mv, Type.getType(methodDefine.define)));
        }
    }
}
