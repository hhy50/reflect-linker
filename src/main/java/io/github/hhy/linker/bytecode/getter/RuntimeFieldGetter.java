package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHolder;
import io.github.hhy.linker.bytecode.vars.FieldVar;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class RuntimeFieldGetter extends Getter<RuntimeFieldRef> {
    public final FieldRef prev;
    public final Type methodType;
    public MethodHandleMember mhMember;

    public RuntimeFieldGetter(String implClass, RuntimeFieldRef field, Type methodType) {
        super(field);
        this.prev = field.getPrev();
        this.methodType = methodType;
        super.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "get_" + field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        this.lookupMember = classImplBuilder.defineLookup(this.prev);
        // 定义当前字段的mh
        this.mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);
        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.methodName, methodHolder.desc, null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    ObjectVar objVar = prev.getter.invoke(methodBody);

                    if (!lookupMember.isTargetLookup()) {
                        // 校验lookup和mh
                        Getter prev = this.prev.getter;
                        staticCheckLookup(methodBody, prev.lookupMember, this.lookupMember, objVar, prev.field);
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
        FieldVar objectVar = new FieldVar(methodBody.lvbIndex++, methodType.getReturnType(), field.fieldName);
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodHolder.owner, methodHolder.methodName, methodHolder.desc, false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }
}
