package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Type;

/**
 * The type Setter.
 *
 * @param <T> the type parameter
 */
public abstract class Setter<T extends FieldRef> extends MethodHandle {

    /**
     * The Field.
     */
    protected final T field;
    /**
     * The Method holder.
     */
    protected MethodHolder methodHolder;

    /**
     * The Method type.
     */
    protected Type methodType;

    /**
     * Instantiates a new Setter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public Setter(String implClass, T field) {
        this.field = field;
        this.methodType = Type.getMethodType(Type.VOID_TYPE, AsmUtil.isPrimitiveType(field.getType()) ? field.getType() : ObjectVar.TYPE);
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "set_"+field.getUniqueName(), this.methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        methodBody.append(new MethodInvokeAction(methodHolder)
                    .setInstance(LoadAction.LOAD0)
                    .setArgs(methodBody.getArgs()));
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        mhMember.store(methodBody, new MethodInvokeAction(Runtime.FIND_SETTER)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass, LdcLoadAction.of(this.field.fieldName)));
    }
}
