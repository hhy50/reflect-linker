package io.github.hhy50.linker.generate.invoker;


import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InlineAction;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import org.objectweb.asm.Opcodes;

/**
 * The type Field ops method handler.
 */
public abstract class FieldOpsMethodHandler extends MethodHandle {
    /**
     * The Mh name.
     */
    protected String fullName;

    /**
     * The Method holder.
     */
    protected MethodDescriptor descriptor;

    /**
     *
     */
    protected InlineAction inlineMhInvoker;

    /**
     * The Lookup class.
     */
    public ClassTypeMember lookupClass;

    /**
     * Instantiates a new Field ops method handler.
     *
     * @param fullName     the fullName
     * @param descriptor the descriptor
     */
    public FieldOpsMethodHandler(String fullName, MethodDescriptor descriptor) {
        this.fullName = fullName;
        this.descriptor = descriptor;
    }

    /**
     * 定义runtime的方法
     *
     * @param classImplBuilder the class impl builder
     * @param runtimeField     the runtime field
     */
    protected void defineRuntimeMethod(InvokeClassImplBuilder classImplBuilder, RuntimeFieldRef runtimeField) {
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(fullName, descriptor.getType());

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(() -> Args.of(0))
                                .then(ownerVar -> checkLookClass(this.lookupClass, ownerVar, null)) // TODO
                                .then(ownerVar -> {
//                                    ClassTypeMember prevLookupClass = preFieldGetter.lookupClass;
//                                    if (prevLookupClass != null) {
//                                        return staticCheckClass(this.lookupClass, prevField.fieldName, prevLookupClass);
//                                    }
                                    return null;
                                })
                                .then(ownerVar -> checkMethodHandle(this.lookupClass, mhMember, ownerVar))
                                .map(runtimeField.isDesignateStatic() ?
                                        (runtimeField.isStatic() ? ownerVar -> mhMember.invokeStatic(Args.loadArgs())
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, Args.loadArgs()))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, Args.loadArgs())
                                ),
                        Actions.areturn(descriptor.getReturnType()));
    }

    /**
     * Define method.
     *
     * @param classImplBuilder the class impl builder
     * @param field            the field
     */
    protected void defineMethod(InvokeClassImplBuilder classImplBuilder, EarlyFieldRef field) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(fullName, null, descriptor.getType());
        clinit.append(initStaticMethodHandle(mhMember,
                loadClass(field.getLookupClass()), field.fieldName, field.getType(), field.isStatic()));
        if (field.isStatic()) {
            this.inlineMhInvoker = (__, argsChain) -> {
                return argsChain.map(mhMember::invokeStatic)
                        .map(Actions::newLocalVar);
            };
        } else {
            this.inlineMhInvoker = (varInstChain, argsChain) -> {
                return varInstChain.map(varInst -> mhMember.invokeInstance(varInst, argsChain))
                        .map(Actions::newLocalVar);
            };
        }
    }
}
