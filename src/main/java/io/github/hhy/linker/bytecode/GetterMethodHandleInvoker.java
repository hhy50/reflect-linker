package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.VarInst;
import io.github.hhy.linker.define.Field;
import io.github.hhy.linker.define.TargetField;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;


public class GetterMethodHandleInvoker extends MethodHandleInvoker {
    private Field field;
    private GetterMethodHandleInvoker prevMh;

    public GetterMethodHandleInvoker(GetterMethodHandleInvoker prev, Field target) {
        super(target.getFullName()+"_getter_mh");
        this.prevMh = prev;
        this.field = target;
    }

    public static GetterMethodHandleInvoker target(String targetClass) {
        return new GetterMethodHandleInvoker(null, new TargetField(targetClass));
    }

    @Override
    public void define(InvokeClassImplBuilder classBuilder) {
        if (prevMh != null) {
            // 先给上层生成lookup和mh
            prevMh.define(classBuilder);

            ImplClassConstruct construct = classBuilder.getConstruct();
            // 想要获取这个成员的getter/setter/invoke, 必须要生成上层变量类型的lookup
            // 比如 a.b, lookup就是a.class
            String prevVar = field.getPrev() == null ? "target" : field.getPrev().getFullName();
            VarInst localVar = construct.getLocalVar(prevVar);

            construct.append((write) -> {

            });

            classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, mhVar, "Ljava/lang/invoke/MethodHandle;", null, null);
            construct.append((write) -> {
                String ownerDesc = ClassUtil.className2path(classBuilder.getClassName());
                write.visitFieldInsn(Opcodes.GETFIELD, ownerDesc, lookup.getVarName(), "Ljava/lang/invoke/MethodHandles/Lookup;");
                prevMh.invoke(write);
                write.visitLdcInsn(fieldName);
                write.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "findGetter",
                        "(Ljava/lang/invoke/MethodHandles/Lookup;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;");
                write.visitFieldInsn(Opcodes.PUTFIELD, ownerDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
            });
        }
    }
}
