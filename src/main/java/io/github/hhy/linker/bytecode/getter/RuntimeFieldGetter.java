package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodRef;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class RuntimeFieldGetter extends Getter {
    private static final Type DEFAULT_METHOD_TYPE = Type.getType("()Ljava/lang/Object;");
    public final RuntimeField prev;
    public final Type methodType;
    private LookupMember lookupMember;
    private MethodHandleMember mhMember;
    private MethodRef methodRef;

    public RuntimeFieldGetter(String implClass, RuntimeField field, Type methodType) {
        super(field);
        this.prev = field.getPrev();
        this.methodType = methodType == null ? DEFAULT_METHOD_TYPE : methodType;
        this.methodRef = new MethodRef(ClassUtil.className2path(implClass), "get_"+field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        this.lookupMember = classImplBuilder.defineLookup(this.prev.getLookupName());
        // 定义当前字段的mh
        this.mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), Type.getMethodType("(Ljava/lang/Object;)"+methodType.getReturnType().getDescriptor()));
        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodRef.methodName, methodRef.desc, null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    ObjectVar objVar = prev.getter.invoke(methodBody);
//                    objVar.checkNullPointer(methodBody, field.getNullErrorVar());
                    // 校验lookup和mh
                    if (!lookupMember.memberName.equals(RuntimeField.TARGET.getLookupName())) {
                        staticCheckLookup(methodBody, lookupMember, objVar);
                        checkLookup(methodBody, lookupMember, mhMember, objVar);
                    }
                    checkMethodHandle(methodBody, lookupMember, mhMember, objVar);
                    // mh.invoke(obj)
                    ObjectVar result = mhMember.invoke(methodBody, objVar);
                    result.load(methodBody);
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType().getDescriptor());
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodRef.owner, methodRef.methodName, methodRef.desc, false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }

    /**
     * 通过一级的class获取字段, 主要是为了静态字段的访问
     * <pre>
     * if (obj == null) {
     *      lookup = Runtime.findLookup(prev_lookup.lookupClass(), 'field');
     * }
     * </pre>
     *
     * @param methodBody
     * @param lookupMember
     * @param objVar
     */
    protected void staticCheckLookup(MethodBody methodBody, LookupMember lookupMember, ObjectVar objVar) {
        methodBody.append((mv) -> {
            Label endLabel = new Label();
            // if (obj == null)
            objVar.load(methodBody);
            mv.visitJumpInsn(IFNONNULL, endLabel); // obj == null

            lookupMember.lookupClass(methodBody); // prev_lookup.lookupClass()
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "findLookup", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/invoke/MethodHandles$Lookup;", false); // Call Runtime.lookup()
            lookupMember.store(methodBody);
            mv.visitLabel(endLabel);
        });
    }
}
