package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.BytecodeClassLoader;
import io.github.hhy.linker.define.InterfaceClassDefine;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;


public class ClassImplGenerator {

    public static Class<?> generateImplClass(InterfaceClassDefine defineClass, final ClassLoader cl) {
        if (cl == null) {
            throw new IllegalArgumentException("classLoader must not be null");
        }
        Class<?> define = defineClass.getDefine();
        Class<?> targetClass = defineClass.getTargetClass();
        String implClassName = define.getName()+"$impl";
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
        return BytecodeClassLoader.load(cl, implClassName, bytecode);
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
