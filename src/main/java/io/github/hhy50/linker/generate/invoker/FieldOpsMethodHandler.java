package io.github.hhy50.linker.generate.invoker;


import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.BiFunction;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;
import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.of;

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
    protected final Type opsFieldType;

    /**
     * 运行时调用的方法
     */
    protected MethodDescriptor rmd;

    /**
     *
     */
    protected BiFunction<VarInst, VarInst[], VarInst> inlineMhInvoker;

    /**
     * The Lookup class.
     */
    public ClassTypeMember lookupClass;

    /**
     * Instantiates a new Field ops method handler.
     *
     * @param fieldName    the field name
     * @param fullName     the full name
     * @param opsFieldType the opsFieldType
     */
    public FieldOpsMethodHandler(String fieldName, String fullName, Type opsFieldType) {
        this.fieldName = fieldName;
        this.fullName = fullName;
        this.opsFieldType = opsFieldType;
    }

    /**
     * defineRuntimeMethod
     *
     * @param classImplBuilder
     * @param isDesignateStatic
     */
    protected void defineRuntimeMethod(InvokeClassImplBuilder classImplBuilder, Boolean isDesignateStatic) {
        String prefix = "getter_";
        if (this.opsFieldType.getReturnType() == Type.VOID_TYPE) {
            prefix = "setter_";
        }

        this.rmd = MethodDescriptor.of(prefix + fullName.replace('.', '_'), TypeUtil.appendArgs(this.opsFieldType, ObjectVar.TYPE, true));

        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(prefix + fullName, this.opsFieldType);
        ChainAction<VarInst> invoker = mapOwnerAndArgs(of(MethodBody::getArgs), (ownerVar, args) -> {
            Action action = isDesignateStatic != null ?
                    (isDesignateStatic ? mhMember.invokeStatic(args) : mhMember.invokeInstance(ownerVar, args))
                    : mhMember.invokeOfNull(ownerVar, args);
            return VarInst.wrap(action, opsFieldType.getReturnType());
        });

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.rmd.getMethodName(), this.rmd.getType(), null)
                .intercept(of(() -> Args.of(0))
                        .then(ownerVar -> checkLookClass(this.lookupClass, ownerVar, null)) // TODO
                        .then(ownerVar -> {
//                                    ClassTypeMember prevLookupClass = preFieldGetter.lookupClass;
//                                    if (prevLookupClass != null) {
//                                        return staticCheckClass(this.lookupClass, prevField.fieldName, prevLookupClass);
//                                    }
                            return null;
                        })
                        .then(ownerVar -> checkMethodHandle(this.lookupClass, mhMember))
                        .andThen(invoker.areturn()));
    }

    /**
     * defineMethod
     *
     * @param classImplBuilder the class impl builder
     * @param lookupClass      the lookup class
     * @param isStatic         the is static
     */
    protected void defineMethod(InvokeClassImplBuilder classImplBuilder, Type lookupClass, boolean isStatic) {
        String prefix = "getter_";
        if (this.opsFieldType.getReturnType() == Type.VOID_TYPE) {
            prefix = "setter_";
        }

        // init methodHandle
        MethodBody clinit = classImplBuilder.getClinit();
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(prefix + fullName, null, this.opsFieldType);
        clinit.append(initStaticMethodHandle(mhMember, loadClass(lookupClass), isStatic));
        if (isStatic) {
            this.inlineMhInvoker = (__, args) -> VarInst.wrap(mhMember.invokeStatic(args));
        } else {
            this.inlineMhInvoker = (varInst, args) -> VarInst.wrap(mhMember.invokeInstance(varInst, args));
        }
    }
}
