package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.MethodHolder;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.FieldVar;
import io.github.hhy.linker.generate.bytecode.vars.LookupMember;
import io.github.hhy.linker.generate.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Type;


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
        // Object a = get_a();
        MethodInvokeAction invoker = new MethodInvokeAction(methodHolder)
                .setInstance(LoadAction.LOAD0);

        FieldVar objectVar = new FieldVar(methodBody.lvbIndex++, methodType.getReturnType(), field.fieldName);
        objectVar.store(methodBody, invoker);
        return objectVar;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        // mh = Runtime.findGetter(lookup, "field");
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupMember, LdcLoadAction.of(field.fieldName));
        mhMember.store(methodBody, findGetter);
    }
}
