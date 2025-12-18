package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;

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
        super(field.getName(), field.getFullName(), Type.getMethodType(field.getType()));
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
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        if (super.inlineMhInvoker != null) {
            return mapOwnerAndArgs(argsAction, super.inlineMhInvoker);
        }
        return argsAction.map(args -> new SmartMethodInvokeAction(new SmartMethodDescriptor(super.runtimeMethodName, super.mhType))
                        .setInstance(LoadAction.LOAD0)
                        .setArgs(args))
                .map(Actions::newLocalVar);
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass) {
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupClass.getLookup(), lookupClass, LdcLoadAction.of(fieldName));
        return mhMember.store(findGetter);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean isStatic) {
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICGETTER : MethodDescriptor.LOOKUP_FINDGETTER);
        findGetter.setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(super.fieldName), loadClass(field.getType()));
        return mhMember.store(findGetter);
    }
}
