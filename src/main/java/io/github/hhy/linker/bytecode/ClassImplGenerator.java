package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.BytecodeClassLoader;
import io.github.hhy.linker.define.InvokeClassDefine;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;


public class ClassImplGenerator {

    private static final BytecodeClassLoader classLoader = new BytecodeClassLoader();

    public static Class<?> generateImplClass(InvokeClassDefine defineClass) {
        Class<?> define = defineClass.define;
        String target = defineClass.bindClass;
        String implClassName = define.getName()+"$impl";
        InvokeClassImplBuilder classBuilder = AsmUtil
                .defineImplClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setTarget(target);

        List<MethodDefine> methodDefines = defineClass.methodDefines;
        methodDefines.sort(Comparator.comparing(MethodDefine::getName));
        for (MethodDefine methodDefine : methodDefines) {
            Method method = methodDefine.define;
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null)
                    .accept(mv -> {
                        generateMethodImpl(classBuilder, mv, methodDefine);
                    });
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        try {
            Files.write(FileSystems.getDefault().getPath("C:\\Users\\hanhaiyang\\IdeaProjects\\reflect-linker\\build\\"+ClassUtil.toSimpleName(implClassName)+".class"), bytecode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classLoader.load(implClassName, bytecode);
    }

    private static void generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodVisitor mv, MethodDefine methodDefine) {
        MethodHandle mh = null;
        if (methodDefine.hasGetter()) {
            mh = BytecodeFactory.generateGetter(classBuilder, methodDefine, methodDefine.fieldRef);
        } else if (methodDefine.hasSetter()) {
            mh = BytecodeFactory.generateSetter(classBuilder, methodDefine, methodDefine.fieldRef);
        } else {
            AsmUtil.throwNoSuchMethod(mv, methodDefine.define.getName());
        }
        if (mh != null) {
            mh.define(classBuilder);
            mh.invoke(new MethodBody(mv, Type.getType(methodDefine.define)));
        }
    }
}
