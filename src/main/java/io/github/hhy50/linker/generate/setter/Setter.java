package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.action.RuntimeAction;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Type;

/**
 * <p>Abstract Setter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class Setter<T extends FieldRef> extends MethodHandle {

    protected final T field;
    protected MethodHolder methodHolder;

    protected Type methodType;

    /**
     * <p>Constructor for Setter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param field a T object.
     */
    public Setter(String implClass, T field) {
        this.field = field;
        this.methodType = Type.getMethodType(Type.VOID_TYPE, AsmUtil.isPrimitiveType(field.getType())? field.getType(): ObjectVar.TYPE);
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "set_"+field.getUniqueName(), this.methodType.getDescriptor());
    }

    /** {@inheritDoc} */
    @Override
    public VarInst invoke(MethodBody methodBody) {
        methodBody.append(() -> {
            return new MethodInvokeAction(methodHolder)
                    .setInstance(LoadAction.LOAD0)
                    .setArgs(methodBody.getArgs());
        });
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        mhMember.store(methodBody, RuntimeAction.findSetter(lookupMember, this.field.fieldName));
    }
}
