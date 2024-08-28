package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.action.RuntimeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Type;

public abstract class Setter<T extends FieldRef> extends MethodHandle {

    protected final T field;
    protected MethodHolder methodHolder;

    protected Type methodType;

    public Setter(String implClass, T field) {
        this.field = field;
        this.methodType = Type.getMethodType(Type.VOID_TYPE, field.getType());
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "set_"+field.getUniqueName(), this.methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        methodBody.append(() -> {
            return new MethodInvokeAction(methodHolder)
                    .setInstance(LoadAction.LOAD0)
                    .setArgs(methodBody.getArgs());
        });
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        mhMember.store(methodBody, RuntimeAction.findSetter(lookupMember, this.field.fieldName));
    }
}
