package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.TargetField;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;


public class GetterMethodHandleHolder extends MethodHandleInvoker {
    private TargetField target;
    private GetterMethodHandleHolder prev;

    public GetterMethodHandleHolder(GetterMethodHandleHolder prev, TargetField target) {
        this.prev = prev;
        this.target = target;
    }

    public static GetterMethodHandleHolder target(String targetClass) {
        return null;
    }

//    public static GetterMethodHandleHolder target(String bindTarget) {
//        return new GetterMethodHandleHolder(null, TargetField.target(bindTarget));
//    }

    @Override
    public void define(InvokeClassImplBuilder classBuilder) {
        if (prev != null) {
            // 先给上层生成lookup和mh
            prev.define(classBuilder);
        }
        if (target.getPrev() != null) {
            Lookup lookup = classBuilder.findLookup(target.getPrev().getFieldName());
            String mhVar = target.getFullName() + "_getter_mh";
            classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, mhVar,
                    "Ljava/lang/invoke/MethodHandle;", null, null);
            classBuilder.appendInit((write) -> {
                String implDesc = ClassUtil.className2path(classBuilder.getClassName());
                write.visitFieldInsn(Opcodes.GETFIELD, implDesc, lookup.getVarName(), "Ljava/lang/invoke/MethodHandles/Lookup;");
                // TODO, 上一层入栈
                // prev
                write.visitLdcInsn(target.getFieldName());
                write.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "findGetter",
                        "(Ljava/lang/invoke/MethodHandles/Lookup;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;");
                write.visitFieldInsn(Opcodes.PUTFIELD, implDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
            });
        }
    }
}
