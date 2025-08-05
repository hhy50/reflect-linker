package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;

/**
 * The type Field ops method handler.
 */
public abstract class FieldOpsMethodHandler extends MethodHandle {
    /**
     * The Mh name.
     */
    protected String mhName;

    /**
     * The Method holder.
     */
    protected MethodDescriptor descriptor;

    /**
     * The Lookup class.
     */
    public ClassTypeMember lookupClass;


    /**
     * Instantiates a new Field ops method handler.
     *
     * @param mhName     the mh name
     * @param descriptor the descriptor
     */
    public FieldOpsMethodHandler(String mhName, MethodDescriptor descriptor) {
        this.mhName = mhName;
        this.descriptor = descriptor;
    }

    /**
     * 定义runtime的方法
     *
     * @param classImplBuilder the class impl builder
     * @param runtimeField     the runtime field
     */
    protected void defineRuntimeMethod(InvokeClassImplBuilder classImplBuilder, RuntimeFieldRef runtimeField) {
        FieldRef prevField = runtimeField.getPrev();
        Getter preFieldGetter = classImplBuilder.getGetter(prevField.getUniqueName());
        preFieldGetter.define(classImplBuilder);

        this.lookupClass = classImplBuilder.defineLookupClass(runtimeField.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(mhName, descriptor.getType());
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(preFieldGetter::invoke)
                                .then((body, ownerVar) -> checkLookClass(body, this.lookupClass, ownerVar, preFieldGetter))
                                .then((body, ownerVar) -> {
                                    ClassTypeMember prevLookupClass = preFieldGetter.lookupClass;
                                    if (prevLookupClass != null) {
                                        staticCheckClass(body, this.lookupClass, prevField.fieldName, prevLookupClass);
                                    }
                                })
                                .then((body, ownerVar) -> checkMethodHandle(body, this.lookupClass, mhMember, ownerVar))
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
        FieldRef prev = field.getPrev();
        Getter getter = classImplBuilder.getGetter(prev.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(mhName, null, descriptor.getType());
        initStaticMethodHandle(clinit, mhMember, loadClass(field.getLookupClass()), field.fieldName, field.getDecalaredType(), field.isStatic());

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept((field.isStatic()
                        ? mhMember.invokeStatic(Args.loadArgs())
                        : ChainAction.of(getter::invoke).then(VarInst::checkNullPointer).map(varInst -> mhMember.invokeInstance(varInst, Args.loadArgs())))
                        .andThen(Actions.areturn(descriptor.getReturnType()))
                );
    }
}
