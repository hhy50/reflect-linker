package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.BytecodeClassLoader;
import io.github.hhy.linker.define.DefaultTargetProviderImpl;
import io.github.hhy.linker.define.InvokeClassDefine;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;


public class ClassImplGenerator {

    private static final BytecodeClassLoader classLoader = new BytecodeClassLoader();

    public static Class<?> generateImplClass(InvokeClassDefine defineClass) {
        Class<?> define = defineClass.getDefine();
        Class<?> target = defineClass.getTarget();
        String implClassName = define.getName() + "$impl";
        InvokeClassImplBuilder classBuilder = AsmUtil
                .defineImplClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "")
                .setTarget(target);

        for (MethodDefine methodDefine : defineClass.getMethodDefines()) {
            Method method = methodDefine.getMethod();
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null)
                    .accept(writer -> {
                        BytecodeGenerator bytecodeGenerator = methodDefine.getBytecodeGenerator();
                        bytecodeGenerator.generate(classBuilder, writer);
                    });
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        return classLoader.load(implClassName, bytecode);
    }
}
