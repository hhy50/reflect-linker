package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ClassTypeVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInstWithLookup;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;
import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.of;

/**
 * The type Setter.
 */
public abstract class Setter<T extends FieldRef> extends FieldOpsMethodHandler {

    /**
     * The Field.
     */
    protected final T field;

    /**
     * Instantiates a new Setter.
     *
     * @param field     the field
     */
    public Setter(T field) {
        super(field.getName(), field.getFullName(), Type.getMethodType(Type.VOID_TYPE, field.getType()));
        this.field = field;
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass) {
        MethodInvokeAction findSetter = new MethodInvokeAction(Runtime.FIND_SETTER)
                .setArgs(lookupClass.getLookup(), lookupClass, LdcLoadAction.of(field.getName()));
        return mhMember.store(findSetter);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean isStatic) {
        MethodInvokeAction findSetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICSETTER : MethodDescriptor.LOOKUP_FINDSETTER)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(super.fieldName), loadClass(Type.getType(field.getActualType())));
        return mhMember.store(findSetter);
    }

    public static class WithEarly extends Setter<EarlyFieldRef> {
        public WithEarly(EarlyFieldRef field) {
            super(field);
        }
        @Override
        protected void define0(InvokeClassImplBuilder classImplBuilder) {
            Type lookupClass = field.getLookupClass();
            boolean isStatic = field.isStatic();
            super.defineMethod(classImplBuilder, lookupClass, isStatic);
        }
        @Override
        public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
            return mapOwnerAndArgs(argsAction, super.inlineMhInvoker);
        }
    }

    public static class WithRuntime extends Setter {
        /**
         * Instantiates a new Getter.
         *
         * @param field the field
         */
        public WithRuntime(FieldRef field) {
            super(field);
        }

        @Override
        protected void define0(InvokeClassImplBuilder classImplBuilder) {
            this.lookupClass = classImplBuilder.defineLookupClass(fullName);
            super.defineRuntimeMethod(classImplBuilder, ((RuntimeFieldRef) field).isDesignateStatic());
        }

        @Override
        public ChainAction<VarInst> invoke(ChainAction<VarInst[]> args) {
            return of(() -> new SmartMethodInvokeAction(super.rmd)
                    .setInstance(LoadAction.LOAD0)
                    .setArgs(makeRuntimeOwner(args))
            );
        }
    }
}
