package io.github.hhy50.linker.generate.invoker;


import io.github.hhy50.linker.define.MethodDescriptor;
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
import org.objectweb.asm.Type;

/**
 * The type Field ops method handler.
 */
public abstract class FieldOpsMethodHandler extends MethodHandle {
    /**
     *
     */
    protected final String fieldName;

    /**
     * The Mh name.
     */
    protected final String fullName;

    /**
     *
     */
    protected final Type fieldType;


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
     * @param fieldName the field name
     * @param fullName  the full name
     * @param fieldType the field type
     */
    public FieldOpsMethodHandler(String fieldName, String fullName, Type fieldType) {
        this.fieldName = fieldName;
        this.fullName = fullName;
        this.fieldType = fieldType;
    }

    /**
     * defineRuntimeMethod
     * @param classImplBuilder
     * @param isDesignateStatic
     */
    protected void defineRuntimeMethod(InvokeClassImplBuilder classImplBuilder, Boolean isDesignateStatic) {
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
                                .then(ownerVar -> checkMethodHandle(this.lookupClass, mhMember))
                                .map(isDesignateStatic != null ?
                                        (isDesignateStatic ? ownerVar -> mhMember.invokeStatic(Args.loadArgs())
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, Args.loadArgs()))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, Args.loadArgs())
                                ),
                        Actions.areturn(descriptor.getReturnType()));
    }

    /**
     *defineMethod
     * @param classImplBuilder the class impl builder
     * @param lookupClass      the lookup class
     * @param isStatic         the is static
     */
    protected void defineMethod(InvokeClassImplBuilder classImplBuilder, Type lookupClass, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(fullName, null, descriptor.getType());
        clinit.append(initStaticMethodHandle(mhMember, loadClass(lookupClass), isStatic));
        if (isStatic) {
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
