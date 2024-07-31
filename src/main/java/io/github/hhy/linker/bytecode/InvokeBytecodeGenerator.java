package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.Target$Method;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.concurrent.atomic.AtomicInteger;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;
import static io.github.hhy.linker.asm.AsmUtil.loadArgs;


public class InvokeBytecodeGenerator implements BytecodeGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private final Target$Method target;

    public InvokeBytecodeGenerator(Target$Method target) {
        this.target = target;
    }

    @Override
    public void generate(AsmClassBuilder classBuilder, MethodVisitor writer) {
        Type methodType = Type.getType(target.getMethod());
        if (!target.isStatic()) {
            writer.visitVarInsn(Opcodes.ALOAD, 0);
        }
        loadArgs(writer, target.isStatic(), methodType.getArgumentTypes());
        if (target.isPrivate() || target.isSuperCall()) {
            String mhVar = target.getMethodName()+"_mh_"+COUNTER.getAndIncrement();
            classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, mhVar,
                            "Ljava/lang/invoke/MethodHandle;", null, null)
                    .writeClint((staticWriter) -> {
                        // mhVar = lookup.findGetter(target.class, "fieldName", fieldType.class);
                        staticWriter.visitFieldInsn(Opcodes.GETSTATIC, ClassUtil.className2path(classBuilder.getClassName()),
                                "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
                        adaptLdcClassType(staticWriter, target);
                        staticWriter.visitLdcInsn(fieldName);
                        adaptLdcClassType(staticWriter, fieldType);
                        staticWriter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;");
                        staticWriter.visitFieldInsn(Opcodes.PUTSTATIC, implDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
                    });

            writer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", methodType.getDescriptor(), false);
        } else if (target.isStatic()) {
            writer.visitMethodInsn(Opcodes.INVOKESTATIC, target.getOwner().getName(),
                    target.getMethodName(), methodType.getDescriptor());
        } else {
            writer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, target.getOwner().getName(),
                    target.getMethodName(), methodType.getDescriptor());
        }
        AsmUtil.areturn(writer, methodType.getReturnType());
    }
}
