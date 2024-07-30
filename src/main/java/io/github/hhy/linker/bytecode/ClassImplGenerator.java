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

public class ClassImplGenerator {

    private static final BytecodeClassLoader classLoader = new BytecodeClassLoader();

    public static Class<?> loadClass(InvokeClassDefine defineClass) {
        Class<?> define = defineClass.getDefine();
        Class<?> target = defineClass.getTarget();
        String implClassName = define.getName()+"$impl";
        AsmClassBuilder classBuilder = AsmUtil.defineClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName,
                DefaultTargetProviderImpl.class.getName(), new String[]{define.getName()}, "");

        // 增加带参的构造函数
        classBuilder.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{target.getName()}, null, "")
                .accept(writer -> {
                    writer.visitVarInsn(Opcodes.ALOAD, 0);
                    writer.visitVarInsn(Opcodes.ALOAD, 1);
                    writer.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    writer.visitInsn(Opcodes.RETURN);
                });
        // 创建lookup字段
        classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "lookup",
                        "Ljava/lang/invoke/MethodHandles$Lookup;", null, null)
                .writeClint(writer -> {
                    // lookup = Util.lookup(target.class);
                    writer.visitLdcInsn(Type.getType(target));
                    writer.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/util/Util", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                    writer.visitFieldInsn(Opcodes.PUTSTATIC, ClassUtil.className2path(implClassName), "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
                });


        for (MethodDefine methodDefine : defineClass.getMethodDefines()) {
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDefine.getMethodName(), methodDefine.getMethodDesc(), null, null)
                    .accept(writer -> {
                        BytecodeGenerator bytecodeGenerator = methodDefine.getBytecodeGenerator();
                        bytecodeGenerator.generate(classBuilder, writer);
                    });
        }
        byte[] bytecode = classBuilder.end().toBytecode();
        return classLoader.load(implClassName, bytecode);
    }
}
