package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.define.Target$Field;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;

public class SetterBytecodeGenerator implements BytecodeGenerator {

    private Type target;
    private String fieldName;
    private Type fieldType;

    public SetterBytecodeGenerator(Target$Field.Setter target) {
        this.target = Type.getType(target.getOwner());
        this.fieldName = target.getFieldName();
        this.fieldType = Type.getType(target.getField().getType());
    }

    /**
     * 生成
     *
     * @param writer
     */
    public void generate(AsmClassBuilder classBuilder, MethodVisitor writer) {
        String implDesc = ClassUtil.className2path(classBuilder.getClassName());

        // 创建methodHandle
        String mhVar = fieldName+"_setter_mh";
        classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, mhVar,
                        "Ljava/lang/invoke/MethodHandle;", null, null)
                .writeClint((staticWriter) -> {
                    // mhVar = lookup.findGetter(target.class, "fieldName", fieldType.class);
                    staticWriter.visitFieldInsn(Opcodes.GETSTATIC, implDesc, "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
                    adaptLdcClassType(staticWriter, target);
                    staticWriter.visitLdcInsn(fieldName);
                    adaptLdcClassType(staticWriter, fieldType);
                    staticWriter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;");
                    staticWriter.visitFieldInsn(Opcodes.PUTSTATIC, implDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
                });

        // 调用 mh.invoke(target, obj)
        writer.visitFieldInsn(Opcodes.GETSTATIC, implDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
        writer.visitVarInsn(Opcodes.ALOAD, 0);
        writer.visitMethodInsn(Opcodes.INVOKEINTERFACE, "io/github/hhy/linker/define/TargetProvider", "getTarget", "()Ljava/lang/Object;");
        writer.visitTypeInsn(Opcodes.CHECKCAST, target.getInternalName());
        writer.visitVarInsn(fieldType.getOpcode(Opcodes.ILOAD), 1); // obj
        writer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", Type.getMethodDescriptor(Type.VOID_TYPE, target, fieldType));
        writer.visitInsn(Opcodes.RETURN);
    }
}
