package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Type;


/**
 * <p>Abstract Getter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class Getter<T extends FieldRef> extends MethodHandle {

    protected final T field;
    protected final String implClass;
    protected MethodHolder methodHolder;
    protected LookupMember lookupMember;
    protected Type methodType;

    /**
     * <p>Constructor for Getter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param field a T object.
     */
    public Getter(String implClass, T field) {
        this.implClass = implClass;
        this.field = field;
        this.methodType = Type.getMethodType(AsmUtil.isPrimitiveType(field.getType())? field.getType():ObjectVar.TYPE);
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "get_"+field.getUniqueName(), this.methodType.getDescriptor());
    }

    /** {@inheritDoc} */
    @Override
    public VarInst invoke(MethodBody methodBody) {
        // Object a = get_a();
        MethodInvokeAction invoker = new MethodInvokeAction(methodHolder)
                .setInstance(LoadAction.LOAD0);
        return methodBody.newLocalVar(methodType.getReturnType(), field.fieldName, invoker);
    }

    /** {@inheritDoc} */
    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        // mh = Runtime.findGetter(lookup, "field");
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupMember, LdcLoadAction.of(field.fieldName));
        mhMember.store(methodBody, findGetter);
    }

    /**
     * <p>Getter for the field <code>lookupMember</code>.</p>
     *
     * @return a {@link io.github.hhy.linker.generate.bytecode.LookupMember} object.
     */
    public LookupMember getLookupMember() {
        return lookupMember;
    }

    /**
     * <p>isTargetGetter.</p>
     *
     * @return a boolean.
     */
    public boolean isTargetGetter() {
        return false;
    }
}
