package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldIndexRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.FieldOpsMethodHandler;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;


/**
 * The type Getter.
 */
public class Getter extends FieldOpsMethodHandler {

    /**
     * The Field.
     */
    protected final FieldRef field;
    /**
     * Instantiates a new Getter.
     *
     * @param field     the field
     */
    public Getter(FieldRef field) {
        super(field.getGetterName(), new SmartMethodDescriptor("get_"+field.getUniqueName(),
                Type.getMethodType(field.getType())));
        this.field = field;
    }

    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (field instanceof RuntimeFieldRef) {
            this.lookupClass = classImplBuilder.defineLookupClass(field.getUniqueName());
            super.defineRuntimeMethod(classImplBuilder, (RuntimeFieldRef) field);
        } else if (field instanceof EarlyFieldRef) {
            super.defineMethod(classImplBuilder, (EarlyFieldRef) field);
        } else if (field instanceof FieldIndexRef) {
            super.defineIndexMethod(classImplBuilder, (FieldIndexRef) field);
        }
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        Action invoker;
        if (super.inlineMhInvoker != null) {
            invoker = super.inlineMhInvoker.invoke();
        } else {
            invoker = new SmartMethodInvokeAction(descriptor)
                    .setInstance(LoadAction.LOAD0);
        }
        return methodBody.newLocalVar(descriptor.getReturnType(), field.fieldName, invoker);
    }

    @Override
    protected void initRuntimeMethodHandle(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass, LdcLoadAction.of(field.fieldName));
        mhMember.store(methodBody, findGetter);
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICGETTER : MethodDescriptor.LOOKUP_FINDGETTER);
        findGetter.setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), loadClass(fieldType));
        mhMember.store(clinit, findGetter);
    }

    public Action checkNull(VarInst varInst) {
        if (field.isNullable()) {
            return Actions.empty();
        }
        return varInst.checkNullPointer();
    }
}
