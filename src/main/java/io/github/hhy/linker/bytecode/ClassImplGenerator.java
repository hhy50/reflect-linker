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
        classBuilder.defineLookup(target.getName());

        // 创建lookup字段
//        classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "lookup",
//                        "Ljava/lang/invoke/MethodHandles$Lookup;", null, null)
//                .writeClint(writer -> {
//                    // lookup = Util.lookup(target.class);
//                    writer.visitLdcInsn(Type.getType(target));
//                    writer.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
//                    writer.visitFieldInsn(Opcodes.PUTSTATIC, ClassUtil.className2path(implClassName), "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
//                });


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
