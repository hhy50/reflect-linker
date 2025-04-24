package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
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
    protected MethodDescriptor descriptor;

    /**
     * Instantiates a new Setter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public Setter(String implClass, T field) {
        this.field = field;
        this.descriptor = MethodDescriptor.of(ClassUtil.className2path(implClass), "set_"+field.getUniqueName(),
                Type.getMethodType(Type.VOID_TYPE, field.getType()));
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        methodBody.append(new MethodInvokeAction(descriptor)
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
