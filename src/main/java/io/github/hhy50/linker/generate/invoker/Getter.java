package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
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
     * @param field the field
     */
    public Getter(FieldRef field) {
        super(field.fieldName, field.getFullName(), field.getType());
        this.field = field;
    }

    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (field instanceof RuntimeFieldRef) {
            this.lookupClass = classImplBuilder.defineLookupClass(fullName);
            super.defineRuntimeMethod(classImplBuilder, ((RuntimeFieldRef) field).isDesignateStatic());
        } else if (field instanceof EarlyFieldRef) {
            Type lookupClass = ((EarlyFieldRef) field).getLookupClass();
            boolean isStatic = ((EarlyFieldRef) field).isStatic();
            super.defineMethod(classImplBuilder, lookupClass, isStatic);
        }
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> __) {
        if (super.inlineMhInvoker != null) {
            return super.inlineMhInvoker.invoke(varInstChain, null);
        }
        return varInstChain.map(varInst -> Actions.newLocalVar(new SmartMethodInvokeAction(descriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(varInst)));
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass, Type mhType) {
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupClass.getLookup(), lookupClass, LdcLoadAction.of(fieldName));
        return mhMember.store(findGetter);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean isStatic) {
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICGETTER : MethodDescriptor.LOOKUP_FINDGETTER);
        findGetter.setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(super.fieldName), loadClass(super.fieldType));
        return mhMember.store(findGetter);
    }
}
