package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.MethodHolder;
import io.github.hhy.linker.bytecode.vars.FieldVar;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public abstract class Getter<T extends FieldRef> extends MethodHandle {

    protected final T field;
    protected final String implClass;
    protected MethodHolder methodHolder;

    protected Type methodType;

    public Getter(String implClass, T field) {
        this.implClass = implClass;
        this.field = field;
        this.methodType = Type.getMethodType(field.getType());
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "get_"+field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        FieldVar objectVar = new FieldVar(methodBody.lvbIndex++, methodType.getReturnType(), field.fieldName);
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodHolder.getOwner(), methodHolder.getMethodName(), methodHolder.getDesc(), false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            lookupMember.load(methodBody); // this.lookup
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC, false);
            mhMember.store(methodBody);
        });
    }
}
