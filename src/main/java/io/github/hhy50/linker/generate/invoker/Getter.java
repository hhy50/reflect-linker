package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;

/**
 * The type Getter.
 */
public abstract class Getter<T extends FieldRef> extends FieldOpsMethodHandler {

    /**
     * The Field.
     */
    protected final T field;

    /**
     * Instantiates a new Getter.
     *
     * @param field the field
     */
    public Getter(T field) {
        super(field.getName(), field.getFullName(), Type.getMethodType(field.getType()));
        this.field = field;
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
                .setArgs(lookupClass, LdcLoadAction.of(super.fieldName), loadClass(Type.getType(field.getActualType())));
        return mhMember.store(findGetter);
    }

    public static class WithEarly extends Getter<EarlyFieldRef> {
        /**
         * Instantiates a new Getter.
         *
         * @param field the field
         */
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

    public static class WithRuntime extends Getter {
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
        public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
            return ChainAction.of(() -> new SmartMethodInvokeAction(super.rmd)
                    .setInstance(LoadAction.LOAD0)
                    .setArgs(argsAction));
        }
    }
}
