package io.github.hhy50.linker.generate.getter;

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
 * <p>Abstract Getter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class Getter<T extends FieldRef> extends MethodHandle {

    protected final T field;
    protected final String implClass;
    protected MethodHolder methodHolder;
    protected Type methodType;
    protected ClassTypeMember typeMember;

    /**
     * <p>Constructor for Getter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param field a T object.
     */
    public Getter(String implClass, T field) {
        this.implClass = implClass;
        this.field = field;
        this.methodType = Type.getMethodType(AsmUtil.isPrimitiveType(field.getType()) ? field.getType(): ObjectVar.TYPE);
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
    protected void mhReassign(MethodBody methodBody, VarInst lookupVar, MethodHandleMember mhMember, VarInst objVar) {
        // mh = Runtime.findGetter(lookup, "field");
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupVar, LdcLoadAction.of(field.fieldName));
        mhMember.store(methodBody, findGetter);
    }


    public ClassTypeMember getTypeMember() {
        return this.typeMember;
    }
}
