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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class RuntimeFieldGetter extends Getter {
    private static final Type DEFAULT_METHOD_TYPE = Type.getType("()Ljava/lang/Object;");
    public final RuntimeField prev;
    public final Type methodType;
    private final LookupMember lookupMember;
    private final MethodHandleMember mhMember;
    private final MethodRef methodRef;

    public RuntimeFieldGetter(String implClass, RuntimeField field, Type methodType) {
        super(field);
        this.prev = field.getPrev();
        this.methodType = methodType == null ? DEFAULT_METHOD_TYPE : methodType;
        this.lookupMember = new LookupMember(ClassUtil.className2path(implClass), this.prev.getFullName()+"_lookup");
        this.mhMember = new MethodHandleMember(ClassUtil.className2path(implClass), field.getGetterMhVarName(), Type.getMethodType("(Ljava/lang/Object;)"+methodType.getReturnType().getDescriptor()));
        this.methodRef = new MethodRef(ClassUtil.className2path(implClass), "get_"+field.getFullName(), methodType.getDescriptor());
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        classImplBuilder.defineField(lookupMember);
        // 定义当前字段的mh
        classImplBuilder.defineField(mhMember);
        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodRef.methodName, methodRef.desc, null, "")
                .accept(writer -> {
                    MethodBody methodBody = new MethodBody(writer, methodType);
                    ObjectVar objVar = prev.getter.invoke(methodBody);
                    objVar.checkNullPointer(methodBody, field.getNullErrorVar());

                    // 校验lookup和mh
                    checkLookup(methodBody, lookupMember, mhMember, objVar);
                    // mh.invoke(obj)
                    ObjectVar result = mhMember.invoke(methodBody, objVar);
                    result.load(methodBody);
                    AsmUtil.areturn(writer, methodType.getReturnType());
                });
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType().getDescriptor());
        methodBody.append(writer -> {
            writer.visitVarInsn(Opcodes.ALOAD, 0);
            writer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodRef.owner, methodRef.methodName, methodRef.desc, false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }
}
