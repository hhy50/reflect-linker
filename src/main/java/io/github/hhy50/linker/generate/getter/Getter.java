package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.FieldRef;
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
 * The type Getter.
 *
 * @param <T> the type parameter
 */
public abstract class Getter<T extends FieldRef> extends MethodHandle {

    /**
     * The Field.
     */
    protected final T field;
    /**
     * The Impl class.
     */
    protected final String implClass;
    /**
     * The Method holder.
     */
    protected MethodDescriptor descriptor;
    /**
     * The Lookup class.
     */
    protected ClassTypeMember lookupClass;

    /**
     * Instantiates a new Getter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public Getter(String implClass, T field) {
        this.implClass = implClass;
        this.field = field;
        this.descriptor = MethodDescriptor.of(ClassUtil.className2path(implClass), "get_"+field.getUniqueName(),
                Type.getMethodType(field.isInvisible() ? ObjectVar.TYPE : field.getType()));
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodInvokeAction invoker = new MethodInvokeAction(descriptor)
                .setInstance(LoadAction.LOAD0);
        return methodBody.newLocalVar(descriptor.getReturnType(), field.fieldName, invoker);
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass, LdcLoadAction.of(field.fieldName));
        mhMember.store(methodBody, findGetter);
    }


    /**
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public ClassTypeMember getLookupClass() {
        return this.lookupClass;
    }
}
